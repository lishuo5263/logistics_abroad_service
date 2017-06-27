package com.ecochain.ledger.util;

import org.apache.commons.codec.binary.Base64;


public class SessionUtil {

    //用户超时分钟
	private static Integer sexp = 30*60;
	
	//验证码超时时间
	private static Integer cexp = 10;
	
	//把用户名入到 Redis中   1小时
	public static void setAttributeForUser(String name, String value) {
	    if(name.contains("app")){
	        JedisUtil.set(name+":"+ LoginConstant.USER_NAME, value, sexp*60*2*24*30);
	    }else{
	        JedisUtil.set(name+":"+ LoginConstant.USER_NAME, value, sexp*60);
	    }
	}

	//取用户信息
    public static String getAttibuteForUser(String name) {
        if(!JedisUtil.exists(name + ":" + LoginConstant.USER_NAME)){
            return "";
        }
        String value = (String) JedisUtil.get(name + ":" + LoginConstant.USER_NAME);
        if(name.contains("app")){
          //重新设置时间 
            if(null != value){
                //一个月
                JedisUtil.set(name+":"+ LoginConstant.USER_NAME, value, sexp*60*2*24*30);
            } 
        }else{
            //重新设置时间 
            if(null != value){
                //30分钟
                JedisUtil.set(name+":"+ LoginConstant.USER_NAME, value, sexp*60);
            } 
        }
        return value;
    }
    //删除用户信息
    public static void delAttibuteForUser(String name) {
        if(JedisUtil.exists(name + ":" + LoginConstant.USER_NAME)){
            JedisUtil.del(name + ":" + LoginConstant.USER_NAME);
        }
    }
    
	public static void setAPPForUser(String name, String value) {
	    JedisUtil.set(name+":"+ LoginConstant.APP_USER_NAME, value, sexp*60*2*24*30);
    }
	
	//取用户信息
    public static String getAPPForUser(String name) {
        if(!JedisUtil.exists(name + ":" + LoginConstant.APP_USER_NAME)){
            return "";
        }
        String value = (String) JedisUtil.get(name + ":" + LoginConstant.APP_USER_NAME);
        //重新设置时间 
        if(null != value){
            //30分钟
            JedisUtil.set(name+":"+ LoginConstant.APP_USER_NAME, value, sexp*60*2*24*30);
        }
        return value;
    }
    //删除用户信息
    public static void delAPPForUser(String name) {
        if(JedisUtil.exists(name + ":" + LoginConstant.APP_USER_NAME)){
            JedisUtil.del(name + ":" + LoginConstant.APP_USER_NAME);
        }
    }
	//把验证码放到Redis中  10分钟
	public static void setAttributeForCode(String name, String value) {
	    JedisUtil.set(name+":"+ LoginConstant.LOGIN_CODE, value, cexp*60);
	}


	
	//取验证码
	public  static String getAttibuteForCode(String name) {
		String vcode = (String) JedisUtil.get(name + ":" + LoginConstant.LOGIN_CODE);
		return vcode;
	}
	public  static void delAttibuteForCode(String name) {
	    JedisUtil.del(name + ":" + LoginConstant.LOGIN_CODE);
    }

	//校验移动端token
	public  static boolean CheckToken(String token) {
		if(StringUtil.isEmpty(token)){
			return false;
		}
		String bt = new String(Base64.decodeBase64(token));
		if(StringUtil.isEmpty(bt)){
			return false;
		}
		String[] bts = bt.split(":");
		if(bts.length > 0){
			String account = bts[0];
			String tomeOutStr = bts[1];
			if(StringUtil.isEmpty(account) || StringUtil.isEmpty(tomeOutStr)){
				return false;
			}
			long currentTime = System.currentTimeMillis();
			if((Long.valueOf(tomeOutStr) - currentTime) < 0){
				return false;
			}
            String serverToken = (String) JedisUtil.get(TokenConstant.TOKEN_CONSTANT+account);
			if(StringUtil.isEmpty(serverToken)){
				return false;
			}
			boolean equals = token.equals(serverToken);
			return equals;
		}
		return false;
	}

	//生成移动端token并存放到redis
	public  static String setAttributeForToken(String accname, String pwd, long expiryOutBySeconds) {
		long loginTime = System.currentTimeMillis();
		String cookieValue = LoginAuthHelper.genAuthCookieValue(accname, pwd, TokenConstant.TOKEN_EXPIRYTIME*1000+loginTime, expiryOutBySeconds);
		System.out.println(cookieValue);
//		redisStringTemplate.setex(TokenConstant.TOKEN_CONSTANT+accname, TokenConstant.TOKEN_EXPIRYTIME.intValue(), cookieValue);
		JedisUtil.set(TokenConstant.TOKEN_CONSTANT+accname, cookieValue, TokenConstant.TOKEN_EXPIRYTIME.intValue());
		return cookieValue;
	}
}
