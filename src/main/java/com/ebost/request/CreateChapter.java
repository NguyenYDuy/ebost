/**
 * 
 */
package com.ebost.request;

/**
 * @author nguyenduy
 *
 */
public class CreateChapter {

	private String title;
	
	private int bookId;
	
	private String content;
	
	private int orders;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getOrders() {
		return orders;
	}

	public void setOrders(int orders) {
		this.orders = orders;
	}
	
	
}
