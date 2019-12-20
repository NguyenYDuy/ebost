/**
 * 
 */
package com.ebost.controller;

import java.util.ArrayList;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ebost.helper.Utils;
import com.ebost.model.Book;
import com.ebost.model.Chapter;
import com.ebost.model.User;
import com.ebost.query.Query;
import com.ebost.request.CreateBook;
import com.ebost.request.CreateChapter;
import com.ebost.request.Register;
import com.ebost.response.BaseResponse;
import com.ebost.response.BookResponse;
import com.ebost.response.ChapterResponse;
import com.ebost.response.Status;
import com.ebost.response.UserResponse;

/**
 * @author nguyenduy
 *
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

	@Autowired
	private Query query;

	@PostMapping("/register")
	public BaseResponse register(@RequestBody Register request) {
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.setStatus(Status.UNAUTHORIZED.getStatus());
		baseResponse.setData(null);
		if (request.getPassword() == null || request.getPassword().isEmpty()) {
			baseResponse.setMessage("Password can't null.");
			return baseResponse;
		}

		if (request.getUsername().length() > 40) {
			baseResponse.setMessage("Username must be 40 characters");
			return baseResponse;
		}

		boolean userExist = query.checkUserExist(request.getUsername());
		if (userExist) {
			baseResponse.setMessage("Username is exist. Please change username.");
			return baseResponse;
		}

		User user = new User();
		user.setUsername(request.getUsername());
		user.setPassword(request.getPassword());
		user = query.createUser(user);
		if (user == null) {
			baseResponse.setMessage("User can't register!!!!!");
			return baseResponse;
		}
		String token = Utils.encodeToken(user);
		UserResponse userResponse = new UserResponse();
		userResponse.setToken(token);
		userResponse.setUserId(user.getUserId());
		userResponse.setUsername(user.getUsername());
		baseResponse.setData(userResponse);
		baseResponse.setStatus(Status.SUCCESSFUL.getStatus());
		baseResponse.setMessage("SUCCESSFUL");
		return baseResponse;
	}

	@PostMapping("/login")
	public BaseResponse login(@RequestBody Register request) {
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.setStatus(Status.UNAUTHORIZED.getStatus());
		baseResponse.setData(null);

		User user = query.getUserByUsername(request.getUsername());
		if (user == null) {
			baseResponse.setMessage("Username or password incorrect.");
			return baseResponse;
		}

		if (BCrypt.checkpw(request.getPassword(), user.getPassword())) {
			String token = Utils.encodeToken(user);
			UserResponse userResponse = new UserResponse();
			userResponse.setToken(token);
			userResponse.setUserId(user.getUserId());
			userResponse.setUsername(user.getUsername());
			baseResponse.setData(userResponse);
			baseResponse.setStatus(Status.SUCCESSFUL.getStatus());
			baseResponse.setMessage("SUCCESSFUL");
			return baseResponse;
		}
		baseResponse.setMessage("Username or password incorrect.");
		return baseResponse;
	}

	@PostMapping("/create-books")
	public BaseResponse createBook(HttpServletRequest request, @RequestBody CreateBook wrapper) {
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.setStatus(Status.UNAUTHORIZED.getStatus());
		baseResponse.setData(null);

		String authorizeHeader = request.getHeader("Authorization");
		String token = authorizeHeader.replace("Bearer ", "");
		String username = Utils.decodeToken(token);
		User user = query.getUserInfo(username);
		if (user == null) {
			baseResponse.setMessage("Wrong token.");
			return baseResponse;
		}

		if (wrapper.getName().length() > 255) {
			baseResponse.setMessage("Book's name must be 255 characters");
			return baseResponse;
		}

		Book book = new Book();
		book.setCreatedBy(user.getUserId());
		book.setName(wrapper.getName());
		book.setViews(0);
		int bookId = query.createBook(book);
		book.setBookId(bookId);
		if (bookId <= 0) {
			baseResponse.setMessage("Wrong!!!");
			return baseResponse;
		}
		baseResponse.setStatus(Status.SUCCESSFUL.getStatus());
		baseResponse.setMessage("SUCCESSFUL");
		BookResponse bookResponse = new BookResponse(book);
		bookResponse.setChapters(new ArrayList<>());
		baseResponse.setData(bookResponse);
		return baseResponse;
	}

	@PostMapping("/create-chapters")
	public BaseResponse createChapter(HttpServletRequest request, @RequestBody CreateChapter wrapper) {

		BaseResponse baseResponse = new BaseResponse();
		baseResponse.setStatus(Status.UNAUTHORIZED.getStatus());
		baseResponse.setData(null);

		String authorizeHeader = request.getHeader("Authorization");
		String token = authorizeHeader.replace("Bearer ", "");
		String username = Utils.decodeToken(token);
		User user = query.getUserInfo(username);
		if (user == null) {
			baseResponse.setMessage("Wrong token.");
			return baseResponse;
		}

		if (wrapper.getTitle().length() > 255) {
			baseResponse.setMessage("Chapter's title must be 255 characters");
			return baseResponse;
		}
		int bookId = query.getBookInfo(wrapper.getBookId());
		if(bookId == 0) {
			baseResponse.setMessage("Book is not found.");
			return baseResponse;
		}
		Chapter chapter = new Chapter();
		chapter.setBookId(wrapper.getBookId());
		chapter.setContent(wrapper.getContent());
		chapter.setTitle(wrapper.getTitle());
		chapter.setOrders(wrapper.getOrders());
		int chapterId = query.createChapter(chapter);
		chapter.setChapterId(chapterId);
		if (chapterId <= 0) {
			baseResponse.setMessage("Wrong!!!");
			return baseResponse;
		}
		baseResponse.setStatus(Status.SUCCESSFUL.getStatus());
		baseResponse.setMessage("SUCCESSFUL");
		ChapterResponse chapterResponse = new ChapterResponse(chapter);

		baseResponse.setData(chapterResponse);
		return baseResponse;

	}

	@GetMapping("/books")
	public BaseResponse getAllBook(HttpServletRequest request) {
		BaseResponse baseResponse = new BaseResponse();
		baseResponse.setStatus(Status.UNAUTHORIZED.getStatus());
		baseResponse.setData(null);

		String authorizeHeader = request.getHeader("Authorization");
		String token = authorizeHeader.replace("Bearer ", "");
		String username = Utils.decodeToken(token);
		User user = query.getUserInfo(username);
		if (user == null) {
			baseResponse.setMessage("Wrong token.");
			return baseResponse;
		}
		Map<Book, List<Chapter>> rsMap = query.getAllBookInfo(user.getUserId());
		List<BookResponse> response = new ArrayList<>();
		for (Entry<Book, List<Chapter>> entry : rsMap.entrySet()) {
			BookResponse bookResponse = new BookResponse(entry.getKey());
			List<ChapterResponse> chapterResponseList = new ArrayList<>();
			for (Chapter chapter : entry.getValue()) {
				ChapterResponse chapterResponse = new ChapterResponse(chapter);
				chapterResponseList.add(chapterResponse);
			}
			bookResponse.setChapters(chapterResponseList);
			response.add(bookResponse);
		}
		response = response.parallelStream().sorted(Comparator.comparing(BookResponse::getBookId).reversed())
				.collect(Collectors.toList());
		baseResponse.setStatus(Status.SUCCESSFUL.getStatus());
		baseResponse.setMessage("SUCCESSFUL");

		baseResponse.setData(response);
		return baseResponse;

	}
}
