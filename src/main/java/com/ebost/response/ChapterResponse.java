/**
 * 
 */
package com.ebost.response;

import com.ebost.model.Chapter;

/**
 * @author nguyenduy
 *
 */
public class ChapterResponse {

	private int chapterId;
	
	private String title;
	
	private String content;
	
	private double rating;
	
	private int status;
	
	private int orders;

	
	
	public ChapterResponse(Chapter chapter) {
		super();
		this.chapterId = chapter.getChapterId();
		this.title = chapter.getTitle();
		this.content = chapter.getContent();
		this.rating = chapter.getRating();
		this.status = chapter.getIsactive();
		this.orders = chapter.getOrders();
	}

	public int getChapterId() {
		return chapterId;
	}

	public void setChapterId(int chapterId) {
		this.chapterId = chapterId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getOrders() {
		return orders;
	}

	public void setOrders(int orders) {
		this.orders = orders;
	}
	
	
}
