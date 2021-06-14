package jp.co.iacsol.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Pattern;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Repository;

import jp.co.iacsol.dao.AuthDao;

@Repository
@ConfigurationProperties(prefix = "app.config")
public class AuthDaoImpl implements AuthDao {

	private String ldapADsPath;
	private String activeDirectorysPath;
	private String domainName;
	private String[][] attributeNameList;

	// @ConfigurationProperties(prefix = "app.config")を利用して設定ファイル(application.yml)から取得
	// ここから
	public String getActiveDirectorysPath() {
		return this.activeDirectorysPath;
	}

	public void setActiveDirectorysPath(String activeDirectorysPath) {
		this.activeDirectorysPath = activeDirectorysPath;
	}

	public String getDomainName() {
		return this.domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public String[][] getAttributeNameList() {
		return this.attributeNameList;
	}

	public void setAttributeNameList(String[][] attributeNameList) {
		this.attributeNameList = attributeNameList;
	}
	// ここまで

	public List<List<String>> getUserData(String samAccountName, String password) {

		ldapADsPath = "ldap://" + activeDirectorysPath;

		// ADから取得する全データを格納する
		Attributes userDataAt = null;

		// 認証時に渡すデータを格納
		Hashtable<String, String> env = new Hashtable<String, String>();

		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapADsPath);
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, samAccountName + "@" + domainName);
		env.put(Context.SECURITY_CREDENTIALS, password);

		try {
			// bind認証する
			DirContext ctx = new InitialDirContext(env);
			// name(識別名･･･DistinguishedName)が必要なのでとりあえずドメインを入れた
			String[] domainNameArray = domainName.split(Pattern.quote("."));
			String name = "DC=" + domainNameArray[0] + ",DC=" + domainNameArray[1];
			// 検索条件にログイン時に入力されたユーザ名を設定
			String filter = "(SamAccountName=" + samAccountName + ")";
			// 検索
			SearchControls cons = new SearchControls();
			cons.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> answer = ctx.search(name, filter, cons);
			SearchResult result = (SearchResult) answer.next();
			// ユーザーの各属性が入ったオブジェクトを取得
			userDataAt = result.getAttributes();
			ctx.close();
		} catch (AuthenticationException ae) {
			// 送信する2次元配列の1番目の配列に「error」の文字列、2番目の配列にエラー内容を格納
			// 仮の設定のため、別の案があれば変更。
			List<List<String>> list = new ArrayList<>(
					Arrays.asList(Arrays.asList("error"), Arrays.asList("AuthenticationException")));
			System.out.println(ae);
			return list;
		} catch (Exception e1) {
			// 送信する2次元配列の1番目の配列に「error」の文字列、2番目の配列にエラー内容を格納
			// 仮の設定のため、別の案があれば変更。
			List<List<String>> list = new ArrayList<>(
					Arrays.asList(Arrays.asList("error"), Arrays.asList("Exception1")));
			System.out.println(e1);
			return list;
		}

		// userDataAtから抽出したデータを格納する
		List<List<String>> userData = new ArrayList<>();
		// 設定ファイルで設定した抽出条件を基にuserDataを作成
		try {
			for (String[] attributeName : attributeNameList) {
				if (attributeName.length == 1) {
					ArrayList<String> attribute = new ArrayList<>();
					attribute.add(attributeName[0]);
					attribute.add(userDataAt.get(attributeName[0]).get().toString());
					userData.add(attribute);

				} else if (attributeName.length >= 2) {
					ArrayList<String> attributes = new ArrayList<>();
					attributes.add(attributeName[1]);
					ArrayList<String> splitAttribute = new ArrayList<String>(
							Arrays.asList(userDataAt.get(attributeName[0]).get().toString().split(",")));
					for (int i = 0; splitAttribute.size() > i;) {
						if (splitAttribute.get(i).startsWith(attributeName[1])) {
							String[] attribute = splitAttribute.get(i).split("=");
							attributes.add(attribute[1]);
							i++;
						} else {
							splitAttribute.remove(i);
						}
					}
					userData.add(attributes);
				}
			}
			return userData;
		} catch (NamingException ne) {
			// 送信する2次元配列の1番目の配列に「error」の文字列、2番目の配列にエラー内容を格納
			// 仮の設定のため、別の案があれば変更。
			List<List<String>> list = new ArrayList<>(
					Arrays.asList(Arrays.asList("error"), Arrays.asList("NamingException")));
			System.out.println(ne);
			return list;
		} catch (Exception e2) {
			// 送信する2次元配列の1番目の配列に「error」の文字列、2番目の配列にエラー内容を格納
			// 仮の設定のため、別の案があれば変更。
			List<List<String>> list = new ArrayList<>(
					Arrays.asList(Arrays.asList("error"), Arrays.asList("Exception2")));
			System.out.println(e2);
			return list;
		}
	}

}