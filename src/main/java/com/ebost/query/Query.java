/**
 * 
 */
package com.ebost.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.ebost.config.Database;
import com.ebost.helper.Utils;
import com.ebost.model.Book;
import com.ebost.model.Chapter;
import com.ebost.model.User;

/**
 * @author nguyenduy
 *
 */
@Component("query")
public class Query {

	private static Database instance = Database.getInstance();

	public boolean checkUserExist(String username) {
		Connection connection = instance.getConnection();
		String query = "SELECT username FROM users WHERE username = ?";
		try (PreparedStatement pstm = connection.prepareStatement(query.toString())) {
			pstm.setString(1, username);
			try (ResultSet rs = pstm.executeQuery()) {

				if (rs.next()) {
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public User createUser(User user) {

		String insert = "INSERT INTO users(user_id, username,password)\n"
				+ "VALUES (nextval('users_sq'),?,?) RETURNING user_id";
		Connection connection = instance.getConnection();
		try (PreparedStatement pstm = connection.prepareStatement(insert)) {
			pstm.setString(1, user.getUsername());
			pstm.setString(2, Utils.hashPassword(user.getPassword()));
			try (ResultSet rs = pstm.executeQuery()) {

				if (rs.next()) {
					user.setUserId(rs.getInt("user_id"));
					return user;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public User getUserByUsername(String username) {
		String query = "SELECT user_id, username, password FROM users WHERE username = ?";
		Connection connection = instance.getConnection();
		try (PreparedStatement pstm = connection.prepareStatement(query)) {
			pstm.setString(1, username);

			try (ResultSet rs = pstm.executeQuery()) {

				if (rs.next()) {
					User user = new User();
					user.setUsername(rs.getString("username"));
					user.setPassword(rs.getString("password"));
					user.setUserId(rs.getInt("user_id"));
					return user;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public User getUserInfo(String username) {
		String query = "SELECT user_id, username FROM users WHERE username = ?";
		Connection connection = instance.getConnection();
		try (PreparedStatement pstm = connection.prepareStatement(query)) {
			pstm.setString(1, username);

			try (ResultSet rs = pstm.executeQuery()) {

				if (rs.next()) {
					User user = new User();
					user.setUsername(rs.getString("username"));
					user.setUserId(rs.getInt("user_id"));

					return user;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public int createBook(Book book) {

		String insert = "INSERT INTO books(book_id, name, description, createdby, views, created,updated) "
				+ "VALUES (nextval('books_sq'),?,?,?,?,now(),now()) RETURNING book_id";
		Connection connection = instance.getConnection();
		try (PreparedStatement pstm = connection.prepareStatement(insert)) {
			pstm.setString(1, book.getName());
			pstm.setString(2, book.getDescription());
			pstm.setInt(3, book.getCreatedBy());
			pstm.setInt(4, book.getViews());

			try (ResultSet rs = pstm.executeQuery()) {

				if (rs.next()) {
					return rs.getInt("book_id");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;

	}

	public int createChapter(Chapter chapter) {
		String insert = "INSERT INTO chapters(chapter_id, title, content, isactive, created, updated, book_id, rating, orders)\n"
				+ "VALUES (nextval('chapters_sq'), ?,?,0,now(),now(),?,0,?) RETURNING chapter_id";
		Connection connection = instance.getConnection();
		try (PreparedStatement pstm = connection.prepareStatement(insert)) {
			pstm.setString(1, chapter.getTitle());
			pstm.setString(2, chapter.getContent());
			pstm.setInt(3, chapter.getBookId());
			pstm.setInt(4, chapter.getOrders());

			try (ResultSet rs = pstm.executeQuery()) {

				if (rs.next()) {
					return rs.getInt("chapter_id");
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 0;
	}

	public Map<Book, List<Chapter>> getAllBookInfo(int userId) {

		String query = "select b.book_id,\n" 
				+ "       b.name,\n" 
				+ "       b.description,\n"
				+ "       b.views,\n"
				+ "       c.rating,\n" 
				+ "       c.title,\n"
				+ "       c.content,\n" 
				+ "       c.orders,\n"
				+ "       c.isactive as chapterstatus,\n" 
				+ "       b.isactive as bookstatus,\n" 
				+ "       c.chapter_id\n" 
				+ "from books b\n"
				+ "left join chapters c on b.book_id = c.book_id and c.isactive >= 0\n"
				+ "where b.createdby = ? and b.isactive >= 0\n" + "order by  book_id desc, c.orders";

		Connection connection = instance.getConnection();
		try (PreparedStatement pstm = connection.prepareStatement(query)) {
			pstm.setInt(1, userId);

			try (ResultSet rs = pstm.executeQuery()) {

				int currentBookId = 0;
				Map<Book, List<Chapter>> rsMap = new HashMap<>();

				Map<Integer, Book> bookMap = new HashMap<>();

				Map<Integer, List<Chapter>> chapterMap = new HashMap<>();
				while (rs.next()) {
					int bookId = rs.getInt("book_id");
					if (bookId != currentBookId) {
						Book book = new Book();
						book.setName(rs.getString("name"));
						book.setDescription(rs.getString("description"));
						book.setIsactive(rs.getInt("bookstatus"));
						book.setViews(rs.getInt("views"));
						book.setBookId(rs.getInt("book_id"));
						bookMap.put(bookId, book);

					}

					List<Chapter> chapterList = chapterMap.get(bookId);
					if (chapterList == null) {
						chapterList = new ArrayList<>();
						chapterMap.put(bookId, chapterList);
					}
					int chapterId = rs.getInt("chapter_id");
					if(rs.wasNull()) {
						continue;
					}
					Chapter chapter = new Chapter();
					chapter.setBookId(bookId);
					chapter.setChapterId(chapterId);
					chapter.setContent(rs.getString("content"));
					chapter.setIsactive(rs.getInt("chapterstatus"));
					chapter.setOrders(rs.getInt("orders"));
					chapter.setRating(rs.getDouble("rating"));
					chapterList.add(chapter);
				}

				bookMap.entrySet().parallelStream().forEach(x -> {
					List<Chapter> chapterList = chapterMap.get(x.getKey());
					if (chapterList == null) {
						chapterList = new ArrayList<>();
					}
					rsMap.put(x.getValue(), chapterList);
				});

				return rsMap;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new HashMap<>();
	}
	
	public int getBookInfo(int bookId) {
		String query = "SELECT book_id FROM books WHERE book_id = ?";
		Connection connection = instance.getConnection();
		try (PreparedStatement pstm = connection.prepareStatement(query)) {
			pstm.setInt(1, bookId);

			try (ResultSet rs = pstm.executeQuery()) {

				if (rs.next()) {
					bookId = rs.getInt("book_id");
					if(rs.wasNull()) {
						return 0;
					}
					
					return bookId;
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}
