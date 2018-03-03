package com.damdev.question.service.impl;

import com.damdev.question.domain.CategoryType;
import com.damdev.question.domain.DocImages;
import com.damdev.question.repository.ApiRepository;
import com.damdev.question.service.ApiService;
import com.damdev.question.service.AuthService;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("ApiService")
public class ApiServiceImpl implements ApiService {

	@Autowired
	AuthService authService;

	@Autowired
	ApiRepository apiRepository;
	
	@Override
	public JSONObject seed(HttpServletRequest req, HttpServletResponse res) {
		JSONObject jsonObj = new JSONObject();

		String token = req.getHeader("Authorization");

		if (authService.checkToken(token)) {
			// 카테고리별 url 만들기
			List<Map<String, Object>> questionList = apiRepository.getSeed();
			System.out.println(questionList);
			for (int i = 0; i < questionList.size(); i++) {
				String url = "/doc/" + questionList.get(i).get("categoryName") + "/" + questionList.get(i)
					.get("questionValue");
				jsonObj.put(questionList.get(i).get("categoryName"), url);
			}
		}

		return jsonObj;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject document(HttpServletResponse response, CategoryType category) {
		JSONObject jsonObj = new JSONObject();
		
		System.out.println(category.getCategory());
		int cnt = apiRepository.selectCategoryHaveChk(category.getCategory());

		if (cnt <= 0) {
			response.setStatus(400);
			jsonObj.put("result_message", "존재하지 않는 카테고리입니다.");
			return jsonObj;
		}
		
		List<DocImages> imageList = apiRepository.selectDocumentImages(category);
		
		String nextUrl = "/doc/"+category.getCategory()+"/";
		
		if(imageList.size() < category.getCnt()) {
			nextUrl += category.getDocId();
		} else {
			nextUrl += imageList.get(imageList.size()-1).getId();
			imageList.remove(imageList.size()-1);
		}
		
		jsonObj.put("next_url", nextUrl);
		jsonObj.put("images", imageList);

		return jsonObj;
	}
}
