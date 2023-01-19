package cn.wildfirechat.app.service;

import cn.wildfirechat.app.enums.AppOperateLogEnum;
import cn.wildfirechat.app.jpa.MemberOperateLog;
import cn.wildfirechat.app.jpa.MemberOperateLogRepository;
import cn.wildfirechat.app.jpa.MemberRepository;
import cn.wildfirechat.app.pojo.LogPairDto;
import cn.wildfirechat.app.pojo.OperateLogList;
import cn.wildfirechat.app.pojo.OperateLogMemoDto;
import cn.wildfirechat.app.tools.FormDataUtil;
import cn.wildfirechat.pojos.InputOutputUserInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
public class LogService {
	
	private static Map<Long, Pair<String,String>> extraApiMap = new HashMap<Long, Pair<String,String>>(); //[[AppOperateLogEnum]]
	static {
		for(AppOperateLogEnum info : AppOperateLogEnum.values()){
//			extraApiMap.put(-100L, Pair.of("/app/login", "用户登录"));//前台
			extraApiMap.put(info.getKey(), Pair.of(info.getApi(), info.getName()));
		}
	}
	
	@Resource
	private ObjectMapper objectMapper;
	
	@Resource
	private MemberRepository memberRepository;

	@Resource
	private MemberOperateLogRepository memberOperateLogRepository;
	
	/**
	 * 新增操作日志
	 * 
	 * @param api			t_admin_auth.Api栏位值(例：/merchant/add)
	 * @param list			log物件
	 */
	public void addOperateLog(InputOutputUserInfo userinfo, String api, OperateLogList list, String ip) {
		addOperateLog(userinfo, api, list, ip, null);
	}
	
	/**
	 * 新增操作日志
	 * 
	 * @param api			t_acl.Api栏位值(例：/merchant/add)
	 * @param list			log物件
	 */
	public void addOperateLog(InputOutputUserInfo userinfo, String api, OperateLogList list, String ip, String merchantId) {
		try {
			AppOperateLogEnum auth = AppOperateLogEnum.parseByApi(api);
			if(auth == null || !auth.getIsLog()) {
				log.info("l表查无对应api=[{}]或该功能不支援记录log", api);
				return;
			}
			// before
			if(list == null || list.isEmpty()) {
				return;
			}
			
			MemberOperateLog record = MemberOperateLog.builder().build();
			record.setUid(userinfo.getUserId());
			record.setAuthId(auth.getKey());
			record.setMemo(objectMapper.writeValueAsString(OperateLogMemoDto.builder().before(list).after(list.getAfter()).build()));
			record.setCreator(userinfo.getName());
			record.setCreateTime(new Date());
			record.setCreatorLevel(0);
			record.setCreatorIp(ip);
			record.setCreatorLocation(getLocationByIP(ip));
			MemberOperateLog result = memberOperateLogRepository.save(record);
			log.info("add operate log result:{}", result.getId());
		}catch (Exception e) {
			log.error("新增操作日志错误api=[{}], exception:{}", api, e.getMessage());
		}
	}

	/**
	 * 新增一笔修改类型操作日志(for Update)
	 *
	 * @param api			t_admin_auth.Api栏位值(例：/merchant/add)
	 * @param list			log物件
	 */
	public void addOperateLogForUpdate(InputOutputUserInfo userinfo, String api, OperateLogList list, String ip) {
		addOperateLogForUpdate(userinfo, api, list, ip, null);
	}
	
	/**
	 * 新增一笔修改类型操作日志(for Update)
	 * 
	 * @param api			t_acl.Api栏位值(例：/merchant/add)
	 * @param list			log物件
	 * @param merchantId	被修改的商户号
	 */
	public void addOperateLogForUpdate(InputOutputUserInfo userinfo, String api, OperateLogList list, String ip, String merchantId) {
		try {

			AppOperateLogEnum auth = AppOperateLogEnum.parseByApi(api);
			if(auth == null || !auth.getIsLog()) {
				log.info("表查无对应api=[{}]或该功能不支援记录log", api);
				return;
			}
			if(list.getAfter().isEmpty()) {
				return;
			}
			
			// 移除before重复key
			LogPairDto copy = null;
			List<LogPairDto> temp = new ArrayList<LogPairDto>(list);
			for(LogPairDto pair : temp) {
				if(copy != null && pair.equals(copy) ) {
					list.remove(pair);
				}
				copy = pair;
			}
			
			MemberOperateLog record = MemberOperateLog.builder().build();
			record.setUid(userinfo.getUserId());
			record.setAuthId(auth.getKey());
			record.setMemo(objectMapper.writeValueAsString(OperateLogMemoDto.builder().before(list).after(list.getAfter()).build()));
			record.setCreator(userinfo.getName());
			record.setCreateTime(new Date());
			record.setCreatorLevel(0);
			record.setCreatorIp(ip);
			record.setCreatorLocation(getLocationByIP(ip));
			MemberOperateLog result = memberOperateLogRepository.save(record);
			log.info("add operate log result:{}", result.getId());
		}catch (Exception e) {
			log.error("新增日志(for update)错误api=[{}], exception:{}", api, e.getMessage());
		}
	}
	

	
	/**
	 * 取得不在t_acl内的但需要增加log的接口
	 * 
	 * Pair.getFirst() = api
	 * Pair.getSecond() = name
	 * 
	 * @return
	 */
	public static Map<Long, Pair<String,String>> getExtraApiMap() {
		return extraApiMap;
	}


	/**
	 * 根据IP取得地理位置
	 *
	 * @return String
	 */
	private String getLocationByIP(String ip){
		StringBuilder sb = new StringBuilder().append("https://www.ip.cn/ip/").append(ip).append(".html");
		String requestUrl = sb.toString();
		log.info("搜寻ip:{} 地理位置, url:{}", ip, requestUrl);

		if(StringUtils.isBlank(ip)){
			return "";
		}
		if(ip.startsWith("192.168.")){
			return "内网IP";
		}

		Map<String, String> headerMap = new LinkedHashMap<>();
		Object request = new Object();
		String location = null;
		try {
			HttpResponse response = FormDataUtil.get(requestUrl, request, headerMap);
			String body = FormDataUtil.getBody(response);
			Document doc = Jsoup.parse(body);
			Element tab0_address = doc.getElementById("tab0_address");//解析tag为<div id="tab0_address">中国 移动</div>

			location = tab0_address.text();
			log.info("解析地理位置为:{}", location);
		} catch (Exception e) {
//            e.printStackTrace();
			log.info("搜寻解析ip地理位置出错, url:{}", requestUrl);
		}

		return location;

	}
	
}

