/**
 * 
 */
package com.ebost.response;

import java.util.List;

import com.ebost.model.Book;

/**
 * @author nguyenduy
 *
 */
public class BookResponse {

	private int bookId;
	
	private String name;
	
	private int createdBy;
	
	private List<ChapterResponse> chapters;
	
	private int status;

	public int getBookId() {
		return bookId;
	}

	public void setBookId(int bookId) {
		this.bookId = bookId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public List<ChapterResponse> getChapters() {
		return chapters;
	}

	public void setChapters(List<ChapterResponse> chapters) {
		this.chapters = chapters;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public BookResponse(Book book) {
		super();
		this.bookId = book.getBookId();
		this.name = book.getName();
		this.status = book.getIsactive();
		this.createdBy = book.getCreatedBy();
	}
	
	
}
