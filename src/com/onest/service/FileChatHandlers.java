package com.onest.service;

import com.onest.bean.FileInfo;
import com.onest.bean.UploadCommand;
import com.onest.config.ServerConfigEnum;
import com.onest.core.FileTarget;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * socket注册handler
 * Created by NIU on 2017/6/26.
 */
public class FileChatHandlers extends AbstractWebSocketHandler {
	private static Map<String,FileTarget> fileTargetMap=new HashMap<String, FileTarget>();
	private boolean isCommandComplete=true;
	private UploadCommand uploadCommand;
	private WebSocketSession session;
	private FileTarget fileTarget;
	ObjectMapper objMapper;

    /**
     * 接到文本消息并且发送出去
     * */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
	    System.out.println("长度："+message.getPayload().toString());
        if(message.getPayloadLength() == 0){
            return;
        }
	    dataProcessing(message.getPayload().toString());
    }

    /**
     * 接到二进制消息并且发送出去
     * */
    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
	    System.out.println("长度："+message.getPayloadLength());
        if(message.getPayloadLength() == 0){
            return;
        }
	    ByteBuffer byteBuffer = message.getPayload();
	    dataProcessing(byteBuffer);
    }

    /**
     * 接到应用程序数据并且发送出去（ping、pong）
     * */
    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
//        super.handlePongMessage(session, message);
//        System.out.println(message);
    }

    /**
     * socket连接建立后的处理
     * */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println(session.getId()+" socket success...");
	    this.session=session;
	    this.session.setTextMessageSizeLimit(ServerConfigEnum.config.getTextSize()+8);
	    this.session.setBinaryMessageSizeLimit(ServerConfigEnum.config.getBlobSize()+8);
	    this.objMapper=new ObjectMapper();
    }

    /**
     * socket连接异常后的处理
     * */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("socket error...");
        if(session.isOpen()){
            session.close();
        }
    }

    /**
     * socket连接关闭后的处理
     * */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("socket closes...");
	    cancelCommand();
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }

	private void dataProcessing(String message){
		if(!this.isCommandComplete){
			cancelCommand();
			System.out.println("command没有完成,已回滚.");
		}
		try {
			JsonParser jp=new JsonFactory().createJsonParser(message);
			FileInfo fileInfo=objMapper.readValue(jp, FileInfo.class);
			sendFirstUploadCommand(fileInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void dataProcessing(ByteBuffer bb){
		this.isCommandComplete=true;
		this.uploadCommand=this.fileTarget.saveByteBuffer(bb, this.uploadCommand).getUploadCommand();
		response(this.uploadCommand);
		this.isCommandComplete=false;
		if(this.uploadCommand.getCompletePercent()==1){
			System.out.println("文件上完成");
			fileUloadComplete();
			this.isCommandComplete=true;
		}
	}
	private void response(String data){
		try {
			this.session.sendMessage(new TextMessage(data));
			System.out.println("data: "+data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void response(Object obj){
		try {
			response(this.objMapper.writeValueAsString(obj));
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void sendFirstUploadCommand(FileInfo fileInfo){
		synchronized (fileTargetMap) {
			if(FileTarget.isFileOK(fileInfo)){
				System.out.println("已存在，无需再次上传_: "+fileInfo.getFileName());
				this.uploadCommand=UploadCommand.getSucccessCommand(fileInfo.getFileId());
			}else{
				this.fileTarget=FileChatHandlers.fileTargetMap.get(fileInfo.getFileId());
				if(this.fileTarget==null){
					System.out.println("第一次上传_: "+fileInfo.getFileName());
					this.fileTarget=new FileTarget(fileInfo);
					this.fileTarget.addUploader(this);
					FileChatHandlers.fileTargetMap.put(fileInfo.getFileId(), this.fileTarget);
				}
				else{
					//分两种情况，一种是没有正在上传的客户端，另一种是有正在上传的客户端。
					System.out.println("断点续传_: "+fileInfo.getFileName());
				}
				this.uploadCommand=this.fileTarget.getUploadCommand();
			}
		}
		response(this.uploadCommand);
		if(this.uploadCommand.getCompletePercent()==1){
			this.isCommandComplete=true;
		}else{
			this.isCommandComplete=false;
		}
	}
	private void cancelCommand(){
		if(this.fileTarget!=null&&this.uploadCommand!=null){
			this.fileTarget.removeUploader(this);
			if(this.uploadCommand.getCompletePercent()!=1){
				this.fileTarget.getSlicedInfo().blobUnComplete(this.uploadCommand.getIndex());
				this.isCommandComplete=true;
				//如果当前没有上传该文件的客户端则释放相应资源并保存进度到磁盘同时也删除map中该文件的target
				if(this.fileTarget.getCurrentFileUploaders().isEmpty()){
					this.fileTarget.getSlicedInfo().saveToDisk();
					this.fileTarget.closeFileWriteAccessChannel();
					fileTargetMap.remove(this.fileTarget.getFileInfo().getFileId());
				}
			}else{
				this.uploadCommand=null;
			}
			this.fileTarget=null;
		}//if end
	}
	private void fileUloadComplete(){
		System.out.println("将本FileServer从文件uploader中移除。");
		this.fileTarget.removeUploader(this);
		if(this.fileTarget.getCurrentFileUploaders().isEmpty()){
			System.out.println("当前文件没有上传客户端，从map中移除该文件的FileTarget。");
			fileTargetMap.remove(this.fileTarget.getFileInfo().getFileId());
		}
	}
}
