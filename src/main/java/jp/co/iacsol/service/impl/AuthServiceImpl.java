package jp.co.iacsol.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.iacsol.dao.impl.AuthDaoImpl;
import jp.co.iacsol.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

	@Autowired
	AuthDaoImpl dao;

	@Override
	public List<List<String>> getUserData(String samAccountName, String password) {
		return dao.getUserData(samAccountName, password);
	}
}
