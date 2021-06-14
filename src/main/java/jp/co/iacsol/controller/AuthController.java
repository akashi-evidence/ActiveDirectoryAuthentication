package jp.co.iacsol.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jp.co.iacsol.service.AuthService;
import jp.co.iacsol.service.impl.AuthServiceImpl;

@RestController
// apiのアドレスの設定…https://サーバーのアドレス/api/auth
@RequestMapping("api/auth")
// 受け取り側のアドレスの指定
// 設定したアドレスからでないとデータを受け取れない。
@CrossOrigin(origins = { "http://localhost:8081" })
public class AuthController {

	private final AuthService authService;

	@Autowired
	public AuthController(AuthServiceImpl authService) {
		this.authService = authService;
	}

	@PostMapping
	public List<List<String>> getUserData(
			@RequestParam("samAccountName") String samAccountName,
			@RequestParam("password") String password) {
		List<List<String>> userData = authService.getUserData(samAccountName, password);
		return userData;
	}
}