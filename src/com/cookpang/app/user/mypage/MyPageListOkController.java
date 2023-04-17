package com.cookpang.app.user.mypage;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.cookpang.app.Execute;
import com.cookpang.app.post.dao.PostDAO;
import com.cookpang.app.post.read.vo.PostReadVO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MyPageListOkController implements Execute {

	@Override
	public void execute(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		
		req.getParameter("userNumber");
		HttpSession session = req.getSession();
		Integer userNumber = (Integer)session.getAttribute("userNumber");
//		int userNumber = 1;
		PostDAO postDAO = new PostDAO();
		int total = postDAO.getTotal();
		//처음 게시판 페이지에 진입하면 페이지에 대한 정보가 없다
		//그러므로 temp에는 null이 들어가게 된다.
		String temp = req.getParameter("page");
		
		//null인 경우는 게시판에 처음 이동하는 것이므로 1페이지를 띄워주면 된다.
		int page = temp == null ? 1 : Integer.valueOf(temp);
		
		//한 페이지에 몇 개의 게시물? 10개
		int rowCount = 9;
		//페이지 버튼 세트는? 5개식
		int pageCount = 5;
		
		/*
		 * 0,10 -> 1페이지 
		 * 10,10 -> 2페이지 
		 * 20,10 -> 3페이지
		 */
		int startRow = (page-1)*rowCount;
		
//		Math.ceil() 올림처리
		int endPage = (int)(Math.ceil(page/(double)pageCount)*pageCount);
//		endPage는 페이지 세트 당 마지막 번호를 의미한다.
		
		int startPage = endPage-(pageCount-1);
//		startPage는 페이지 세트 당 첫 번째 번호를 의미한다.
		
		int realEndPage = (int)Math.ceil(total/(double)rowCount); //total : 게시글의 총 갯수
//		realEndPage는 전체 페이지 중 가장 마지막 번호를 의미한다.		
		
		endPage = endPage > realEndPage? realEndPage : endPage;
//		첫 번째 페이지 세트가 1~5
//		두 번째 페이지 세트가 6~10 이어도
//		realEndPage가 7이라면 두 번째 페이지 세트의 마지막 번호는 7이어야 한다.
		
		boolean prev = startPage>1;
		boolean next = endPage != realEndPage;
		
			
		Map<String, Integer>pageMap = new HashMap();
		
		pageMap.put("startRow", startRow);
		pageMap.put("rowCount", rowCount);
		pageMap.put("userNumber", userNumber);
		
		List <PostReadVO> posts= postDAO.selectAll(pageMap);
		
		req.setAttribute("postList", posts);
		req.setAttribute("page", page);
		req.setAttribute("startPage", startPage);
		req.setAttribute("endPage", endPage);
		req.setAttribute("prev", prev);
		req.setAttribute("next", next);
		
		// Gson 객체를 생성하고, 이를 이용해 위에 설정한 리스트를 json 형태로 변환한다.
	      Gson gson = new Gson();
	     
	     
//JsonArray 객체를 생성하고, 변수를 설정하여 할당한다. 
	      JsonArray postList = new JsonArray();
	      
	    
/*//DAO객체를 생성할 때 만든 리스트를 stream() 메소드를 이용해 순차적으로 처리하고,
각 요소를 gson::toJson() 메소드를 이용해 JSON형태로 변환한 후
JsonParser::parseString() 메소드를 이용해 JsonElement 형태로 파싱합니다.
마지막으로 JsonArray의 변수에 각 JsonElement를 add()메소드를 통해 추가한다.	*/      
	      posts.stream()
	      .map(gson::toJson)
	      .map(JsonParser::parseString)
	      .forEach(postList::add);
	      
	     
/*//JsonObject 객체를 생성하고 이를 이용해 JSON 형태의 결과값을 저장한다
result라는 객체에는 list, startPage, endPage, page , realEndPage라는 key와 그에 해당하는 값들이 저장된다.*/
	      JsonObject result = new JsonObject();
	      
	      result.add("list", JsonParser.parseString(postList.toString()));
	      
	      result.addProperty("startPage", startPage);
	      result.addProperty("endPage", endPage);
	      result.addProperty("page", page);
	      result.addProperty("realEndPage", realEndPage);
	      
	      
	      
	      resp.setContentType("application/json; charSet=utf-8");
	
/*//PrintWriter 객체를 생성하고, result 객체를 문자열로 변환한 후
print()메소드를 통해 결과값을 출력한다. 마지막으로 close()메소드를 통해 출력 스트림을 닫습니다.  */    
	      PrintWriter out = resp.getWriter();
	      out.print(result.toString());
	      out.close();
	      
		
		

	
		
		
//		req.getRequestDispatcher("/app/mypage.jsp").forward(req, resp);
	}

}
