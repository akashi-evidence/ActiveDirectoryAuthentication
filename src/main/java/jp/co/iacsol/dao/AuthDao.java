package jp.co.iacsol.dao;

import java.util.List;

public interface AuthDao {

	public List<List<String>> getUserData(String samAccountName, String password);
}
