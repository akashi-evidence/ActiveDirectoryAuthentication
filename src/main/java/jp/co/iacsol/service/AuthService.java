package jp.co.iacsol.service;

import java.util.List;

public interface AuthService {

	public List<List<String>> getUserData(String samAccountName, String password);
}
