package com.blobcity.model;

import com.google.gson.annotations.SerializedName;

public class BranchesItem{

	@SerializedName("cd")
	private String cd;

	@SerializedName("__v")
	private int V;

	@SerializedName("topic")
	private Topic topic;

	@SerializedName("_id")
	private String id;

	@SerializedName("ud")
	private String ud;

	public void setCd(String cd){
		this.cd = cd;
	}

	public String getCd(){
		return cd;
	}

	public void setV(int V){
		this.V = V;
	}

	public int getV(){
		return V;
	}

	public void setTopic(Topic topic){
		this.topic = topic;
	}

	public Topic getTopic(){
		return topic;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setUd(String ud){
		this.ud = ud;
	}

	public String getUd(){
		return ud;
	}

	@Override
 	public String toString(){
		return 
			"BranchesItem{" + 
			"cd = '" + cd + '\'' + 
			",__v = '" + V + '\'' + 
			",topic = '" + topic + '\'' + 
			",_id = '" + id + '\'' + 
			",ud = '" + ud + '\'' + 
			"}";
		}
}