package com.wsg.robot;

public class ReplyConfig {
	private String match;
	private String replyType;
	private String replyContent;
	
	
	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}

	public String getReplyType() {
		return replyType;
	}

	public void setReplyType(String replyType) {
		this.replyType = replyType;
	}

	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	public ReplyConfig (String match,String type,String content) {
		this.match = match;
		this.replyType = type;
		this.replyContent = content;
	}
	
	public boolean matches(String msg) {
		return msg.matches(this.match);
	}
	
	public String getReply(String msg,String sender) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if ("Text".equals(this.replyType) || "PLAIN".equals(this.replyType)) {
			return this.replyContent;
		} else {
			IReply rep = (IReply)Class.forName(replyContent).newInstance();
			return rep.reply(msg,sender);
		}
	}
}

