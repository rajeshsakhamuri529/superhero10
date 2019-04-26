package com.blobcity.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CoursesResponseModel{

	@SerializedName("Courses")
	private List<CoursesItem> courses;

	public void setCourses(List<CoursesItem> courses){
		this.courses = courses;
	}

	public List<CoursesItem> getCourses(){
		return courses;
	}

	@Override
 	public String toString(){
		return 
			"CoursesResponseModel{" + 
			"courses = '" + courses + '\'' + 
			"}";
		}
}