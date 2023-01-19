package cn.wildfirechat.app.tools;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class StringUtil extends StringUtils {

	private static final char[] HEX = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	private static final char[] BINARY = new char[] { '0', '1' };

	/**
	 * 銀行卡號字串遮罩
	 * 
	 * @param str 銀行卡號
	 * @return
	 */
	public static String maskPayeeCard(String str) {
		if (isNotBlank(str)) {
			return mask(str, 3, str.length() - 3);
		}
		return str;
	}

	/**
	 * 字串遮罩
	 * 
	 * @param str   原始字串
	 * @param start 起始位置(從1開始)
	 * @param end   結束位置
	 * @return
	 */
	public static String mask(String str, int start, int end) {
		if (isNotBlank(str) && str.trim().length() == (start + end)) {
			String maskStr = repeat("*", end - start);
			return overlay(str, maskStr, start, end);
		}
		return str;
	}

	/**
	 * 訂單紀錄remark
	 * 
	 * @param prefix   中文說明(例：渠道方請求失敗,渠道订单成功)
	 * @param request  向渠道方請求內容
	 * @param response 渠道方響應內容
	 * @return
	 */
	public static String getOrderPullRemark(String prefix, Object request, Object response) {
		return prefix + ", 备注讯息：request:" + request + ", response:" + response;
	}

	/**
	 * 字串四捨五入到小數點下兩位，最後加上符號<br>
	 * 
	 * @param str    數字字串
	 * @param symbol 符號(例 : %)
	 * @return
	 */
	public static String toRate(String str, String symbol) {
		boolean matches = str.matches("^\\d+(\\.\\d+)?$");
		if (!matches) {
			return null;
		}
		str = new BigDecimal(str).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
		return str + symbol;
	}

	/**
	 * 首字母转小写
	 * 
	 * @return
	 */
	public static String lowerCaseInitial(String val) {
		if (val == null) {
			return val;
		}
		char[] chars = val.toCharArray();
		char lowerChar = Character.toLowerCase(chars[0]);
		chars[0] = lowerChar;
		return new String(chars);
	}

	/**
	 * 首字母转大写
	 * 
	 * @param val
	 * @return
	 */
	public static String upperCaseInitial(String val) {
		if (val == null) {
			return val;
		}
		char[] chars = val.toCharArray();
		char upperChar = Character.toUpperCase(chars[0]);
		chars[0] = upperChar;
		return new String(chars);
	}

	/**
	 * 将byte数组转化为String类型的十六进制编码格式 本方法实现的思路是：<br>
	 * 1）每位byte数组转换为2位的十六进制数<br>
	 * 2）将字节左移4位取得高四位字节数值，获取对应的char类型数组编码<br>
	 * 3）将字节与0x0F按位与，从而获取第四位的字节，同样获取编码
	 */
	public static String hex(byte[] b) {
		char[] rs = new char[b.length * 2];
		for (int i = 0; i < b.length; i++) {
			rs[i * 2] = HEX[b[i] >> 4 & 0x0F]; // &0x0F的目的是为了转换负数
			rs[i * 2 + 1] = HEX[b[i] & 0x0F];
		}
		return new String(rs);
	}

	/**
	 * 将二进制字符串转换为十六进制字符串
	 * 
	 * @param bin
	 * @return
	 */
	public static String bin2hex(String bin) {
		if (bin == null || "".equals(bin)) {
			return null;
		}
		return Long.toHexString(Long.parseLong(bin, 2));
	}
	
	/**
	 * log字串遮罩
	 * 
	 * @param str   原始字串
	 * @param start 起始位置(從1開始)
	 * @param end   結束位置
	 * @return
	 */
	public static String logMask(String str) {
		if (isNotBlank(str)) {
			return mask(str, 2, str.length() - 2);
		}
		return str;
	}
	
	/**
	 * Query String转map<br>
	 * 
	 * Key和value不decode
	 * 
	 * @param queryString
	 * @return
	 */
	public static Map<String, String> queryStringToMap(String queryString) {
		return queryStringToMap(queryString, false);
	}

	/**
	 * Query String转map
	 * 
	 * @param queryString	
	 * @param decode		是否需要URLDecoder.decode(UTF-8)
	 * @return
	 */
	public static Map<String, String> queryStringToMap(String queryString, boolean decode) {
		if(isBlank(queryString)) {
			return null;
		}
		Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		try {
		    String[] pairs = queryString.split("&");
		    for (String pair : pairs) {
		        int idx = pair.indexOf("=");
		        if(decode) {
		        	query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		        } else {
		        	query_pairs.put(pair.substring(0, idx), pair.substring(idx + 1));
		        }
		    }
		}catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("query string to map exception:" + e.getMessage());
		}
	    return query_pairs;
	}
	
	/**
	 * map转Query String
	 * 
	 * @param map
	 * @return
	 */
	public static String mapToQueryString(Map<String, String> map) {
		if(map == null) {
			return null;
		}
		if(map.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for(Entry<String, String> entry : map.entrySet()) {
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		return sb.deleteCharAt(sb.length() - 1).toString();
	}

	/**
	 * String转map
	 * @param str
	 * @return
	 */
	public static Map<String,String> getStringToMap(String str) {
		if(isBlank(str)) {
			return null;
		}
		//根据逗号截取字符串数组
		String[] str1 = str.split(",");
		//创建Map对象
		Map<String, String> map = new HashMap<>();
		//循环加入map集合
		for (int i = 0; i < str1.length; i++) {
			//根据":"截取字符串数组
			String[] str2 = str1[i].split(":");
			//str2[0]为KEY,str2[1]为值
			map.put(str2[0], str2[1]);
		}
		return map;
	}

	public static String getRequestDataByStream(HttpServletRequest request) {
		BufferedReader br = null;

		try {
			//System.out.println(IOUtils.toString(request.getInputStream(),"UTF-8"));
			br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
		} catch (IOException e) {
			System.out.println("读取参数出错" + e.getMessage());
			e.printStackTrace();
		}

		String line = null;
		StringBuilder sb = new StringBuilder();
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			System.out.println("读取参数出错" + e.getMessage());
			e.printStackTrace();
		}

		System.out.println("传化参数回调：" + line);
		return line;
	}

}
