package com.example.music;

public class Song {
	String name;
	long duration;
	String path;
	String singer;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getSinger() {
		return singer;
	}
	public void setSinger(String singer) {
		this.singer = singer;
	}
	public Song(String name, long duration, String path, String singer) {
		super();
		this.name = name;
		this.duration = duration;
		this.path = path;
		this.singer = singer;
	}
	public Song() {
		super();
	}
	
}
