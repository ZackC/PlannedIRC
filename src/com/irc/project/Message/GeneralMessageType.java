package com.irc.project.Message;

public interface GeneralMessageType {
	public abstract String getHeader();

	public abstract String getContent();

	public abstract void setHeader(String newHeader);
}
