package com.hundsun.gapsv5.svn.bean;

public class GitLogVersion {
	
	private String id;
	
	private String author;
	
	private String authoredDate;
	
	private String committer;
	
	private String committedDate;
	
	private String message;
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getAuthoredDate() {
		return authoredDate;
	}

	public void setAuthoredDate(String authoredDate) {
		this.authoredDate = authoredDate;
	}

	public String getCommitter() {
		return committer;
	}

	public void setCommitter(String committer) {
		this.committer = committer;
	}

	public String getCommittedDate() {
		return committedDate;
	}

	public void setCommittedDate(String committedDate) {
		this.committedDate = committedDate;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
