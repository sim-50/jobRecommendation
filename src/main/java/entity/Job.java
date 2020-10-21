package entity;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

//high level idea: builder pattern concept

//What we want?
//1. Job is read only for client, cannot set!
//2. Set job by a single and simple way!

//How to new object under builder pattern? 
//JobBuilder builder = new JobBuilder();         //default with all null fields.
//the following are all setting fields process
//builder.setJobId("id")                      
//		 .setName("name")
//		 .setAddress("Pittsburgh");
//...
//Job job = builder.build();                     // let builder object to job object
//inside build() method, it has automatically return new Job(this)


//after .build() method, job would be never changed.
//public class Job {
//	private String jobId;
//	private String name;
//	private String address;
//	private Set<String> keywords;
//	private String imageUrl;
//	private String url;
//	//constructor used for create job object
//	//notice the access modifier: here is private!! 
//	private Job(JobBuilder builder) {
//		this.jobId = builder.jobId;
//		this.name = builder.name;
//		this.address = builder.address;
//		this.imageUrl = builder.imageUrl;
//		this.url = builder.url;
//		this.keywords = builder.keywords;
//	}
//	
//	public String getJobId() {
//		return jobId;
//	}
//	public String getName() {
//		return name;
//	}
//	public String getAddress() {
//		return address;
//	}
//	public Set<String> getKeywords() {
//		return keywords;
//	}
//	public String getImageUrl() {
//		return imageUrl;
//	}
//	public String getUrl() {
//		return url;
//	}
//	//builder pattern (nested class)
//	//why does the pattern is fix process?
//	//step1: define a inner class of jobBuilder 
//	//why inner? private constructor in order to only provide single way to create Job object!!
//	//why static? create JobBuilder without job object. JobBuilder first then create job object.
//	public static class JobBuilder{
//		//step2: add fields
//		private String jobId;
//		private String name;
//		private String address;
//		private Set<String> keywords;
//		private String imageUrl;
//		private String url;
//		//step3: add setters toward each field
//		// these method would not affect the value in item even though you set it repeatedly!
//		public void setJobId(String jobId) {
//			this.jobId = jobId;
//			return this;
//		}
//		public void setName(String name) {
//			this.name = name;
//			return this;
//		}
//		public void setAddress(String address) {
//			this.address = address;
//			return this;
//		}
//		public void setKeywords(Set<String> keywords) {
//			this.keywords = keywords;
//			return this;
//		}
//		public void setImageUrl(String imageUrl) {
//			this.imageUrl = imageUrl;
//			return this;
//		}
//		public void setUrl(String url) {
//			this.url = url;
//			return this;
//		}
//		//step4: define a build function: used to return Job object
//		public Job build() {
//			return new Job(this);
//		}
//	}

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Job {
	private String itemId;
	private String name;
	private String address;
	private Set<String> keywords;
	private String imageUrl;
	private String url;
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		obj.put("item_id", itemId);
		obj.put("name", name);
		obj.put("address", address);
		obj.put("keywords", new JSONArray(keywords));
		obj.put("image_url", imageUrl);
		obj.put("url", url);
		return obj;
	}
}

