package com.ecochain.ledger.web.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ecochain.ledger.annotation.LoginVerify;
import com.ecochain.ledger.base.BaseWebService;
import com.ecochain.ledger.constants.CodeConstant;
import com.ecochain.ledger.constants.Constant;
import com.ecochain.ledger.constants.CookieConstant;
import com.ecochain.ledger.model.Page;
import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.service.SendVodeService;
import com.ecochain.ledger.service.ShopOrderInfoService;
import com.ecochain.ledger.service.UserLoginService;
import com.ecochain.ledger.service.UserService;
import com.ecochain.ledger.service.UsersDetailsService;
import com.ecochain.ledger.util.AjaxResponse;
import com.ecochain.ledger.util.Base64;
import com.ecochain.ledger.util.InternetUtil;
import com.ecochain.ledger.util.MD5Util;
import com.ecochain.ledger.util.RequestUtils;
import com.ecochain.ledger.util.SessionUtil;
import com.ecochain.ledger.util.StringUtil;
import com.ecochain.ledger.util.Validator;
import com.github.pagehelper.PageInfo;
import com.sun.jna.Library;
import com.sun.jna.Native;

/*
 * 总入口
 */
@RestController
@RequestMapping(value = "/api/user")
@Api(value = "用户Service")
public class UsersWebService extends BaseWebService {

    @Autowired
    private UserLoginService userLoginService;
    @Autowired
    private UserService userService;
    @Autowired
    private SendVodeService sendVodeService;
    @Autowired
    private ShopOrderInfoService shopOrderInfoService;
    
    @Resource(name="userDetailsService")
    private UsersDetailsService userDetailsService;
    
    /* ==================================================登录过滤========================================================== */
    
    /* ==================================================登录过滤========================================================== */
    
    
    /**
     * 获取登录用户的IPinfomodel
     * @throws Exception 
     */
/*  public void getRemortIP(String USERNAME) throws Exception {  
        PageData pd = new PageData();
        HttpServletRequest request = this.getRequest();
        String ip = "";
        if (request.getHeader("x-forwarded-for") == null) {  
            ip = request.getRemoteAddr();  
        }else{
            ip = request.getHeader("x-forwarded-for");  
        }
        pd.put("USERNAME", USERNAME);
        pd.put("IP", ip);
        userDetailsService.saveIP(pd);
    }  */
    

    /**
     * @describe:查询接收转账资产账户信息
     * @author: lishuo
     * @date: 2017年3月10日09:49:02
     * @param request
     * @return: AjaxResponse
     */
    @RequestMapping(value="/findAcceptInfo",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResponse findAcceptInfo(HttpServletRequest request, Page page)throws Exception{
        AjaxResponse ar = new AjaxResponse();
        PageData pd = new PageData();
        pd = this.getPageData();
        try {
            if(StringUtil.isNotEmpty(pd.getString("real_account"))){
                PageData pageData =this.userDetailsService.findAcceptInfo(pd);
                if(pageData==null ||pageData.get("user_id")==null){
                    return fastReturn("接口参数异常,账户"+pd.getString("real_account")+"缺少rela_user_id,资产转入转出失败！", false, "接口参数异常,账户"+pd.getString("real_account")+"缺少rela_user_id,资产转入转出失败！", CodeConstant.NEED_ACCEPT_INTO);
                }else if(pageData.get("public_key")==null){
                    return  fastReturn("接口参数异常,账户"+pd.getString("real_account")+"缺少relauser_public_key,资产转入转出失败！", false, "接口参数异常,账户"+pd.getString("real_account")+"缺少relauser_public_key,资产转入转出失败！", CodeConstant.NEED_ACCEPT_INTO);
                }else{
                    return fastReturn(pageData,true,"查询接收转账资产账户信息成功！", CodeConstant.SC_OK);
                }
            }else{
                ar = fastReturn("接口参数异常,缺少property_id,查询接收转账资产账户信息成功失败！", false, "接口参数异常,缺少property_id,查询接收转账资产账户信息成功失败！", CodeConstant.PARAM_ERROR);
            }
        }catch (Exception e){
            e.printStackTrace();
            ar = fastReturn("系统异常,资产转入转出失败！", false, "系统异常,资产转入转出失败！", CodeConstant.SYS_ERROR);
        }
        return ar;
    }
    
    @RequestMapping(value="/listPageUser",method = RequestMethod.POST)
    @ResponseBody
    public AjaxResponse listPageUser(HttpServletRequest request)throws Exception{
        AjaxResponse ar = new AjaxResponse();
        PageData pd = this.getPageData();
        Map<String,Object> data = new HashMap<String,Object>();
        List<PageData> listPageUser = userDetailsService.listPageUsers(pd);
        data.put("pageInfo", new PageInfo<PageData>(listPageUser));
        /*data.put("pd", pd);
        data.put("page", pd.getPage());
        data.put("rows", pd.getRows());*/
        ar.setSuccess(true);
        ar.setData(data);
        return ar;
    }
	
	
	/**
	 * 请求登录，验证用户
	 */
	@PostMapping("/login")
	@ApiOperation(nickname = "登陆接口", value = "用户登陆", notes = "用户登陆！")
	@ApiImplicitParams({
        @ApiImplicitParam(name = "account", value = "登陆账号", required = true, paramType = "query", dataType = "String"),
        @ApiImplicitParam(name = "password", value = "密码", required = true, paramType = "query", dataType = "String")
	})
	public AjaxResponse login(HttpServletRequest request,HttpServletResponse response){
	    AjaxResponse ar = new AjaxResponse();
		Map<String,Object> data  = new HashMap<String,Object>();
		PageData pd = new PageData();
		pd = this.getPageData();
		try {
            String account = StringUtil.isEmpty(pd.getString("account"))?null:pd.getString("account").trim();
            String password = StringUtil.isEmpty(pd.getString("password"))?null:pd.getString("password").trim();
            if(StringUtil.isEmpty(account)){
                ar.setSuccess(false);
                ar.setMessage("请输入登陆账号！");
                ar.setErrorCode(CodeConstant.USER_NO_EXISTS);
                return ar;
            }
            if(StringUtil.isEmpty(password)){
                ar.setSuccess(false);
                ar.setMessage("请输入密码！");
                ar.setErrorCode(CodeConstant.ERROE_PASSWORD_NULL);
                return ar;
            }
            pd.put("account", account);
            password = MD5Util.getMd5Code(password);
            pd.put("password", password);
            pd = userDetailsService.getUserByAccAndPass(pd,Constant.VERSION_NO);
            if(pd != null){
                if("1".equals(pd.getString("status"))){
                    pd.put("lastlogin_ip", InternetUtil.getRemoteAddr(request));
                    pd.put("lastlogin_port", InternetUtil.getRemotePort(request));
                    userDetailsService.updateLoginTimeById(pd,Constant.VERSION_NO);
                    
                    PageData userInfo = userDetailsService.getUserInfoByUserId((Integer)pd.get("user_id"),Constant.VERSION_NO);
                    String sessionId = RequestUtils.getRequestValue(CookieConstant.CSESSIONID,request);
                    SessionUtil.setAttributeForUser(sessionId, JSON.toJSONString(userInfo));
                    data.put("CSESSIONID", Base64.getBase64(sessionId));
                    ar.setData(data);
                    ar.setSuccess(true);
                    ar.setMessage("登陆成功！");
                    return ar;
                }else{
                    ar.setSuccess(false);
                    ar.setErrorCode(CodeConstant.ERROR_BLACK);
                    ar.setMessage("您已被加入黑名单，如有疑问请联系官方客服！");
                    return ar;
                }
            }else{
                ar.setSuccess(false);
                ar.setErrorCode(CodeConstant.USERNAMEORPWD_ERROR);
                ar.setMessage("账号或密码有误！");
                return ar;
            }
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setMessage("网络繁忙，请稍候重试！");
            ar.setErrorCode(CodeConstant.SYS_ERROR);
        }
        return ar;
    }
    
    /**
     * @describe:注册
     * @author: zhangchunming
     * @date: 2017年3月2日下午3:36:32
     * @param request
     * @param response
     * @return: AjaxResponse
     */
    @PostMapping("/register")
    @ApiOperation(nickname = "用户注册", value = "用户注册", notes = "用户注册")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "account", value = "登陆账号，仅支持手机号注册", required = true, paramType = "query", dataType = "String"),
        @ApiImplicitParam(name = "password", value = "密码，6-16位数字", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse register(HttpServletRequest request,HttpServletResponse response){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data  = new HashMap<String,Object>();
        try {
            PageData pd = new PageData();
            pd = this.getPageData();
            logger.info("--------------register  pd value is "+pd.toString());
            String account = StringUtil.isEmpty(pd.getString("account"))?null:pd.getString("account").trim();
            String password = StringUtil.isEmpty(pd.getString("password"))?null:pd.getString("password").trim();
            if(StringUtil.isEmpty(account)){
                ar.setSuccess(false);
                ar.setMessage("请输入登陆账号！");
                ar.setErrorCode(CodeConstant.USER_NO_EXISTS);
                return ar;
            }
            if(!Validator.isMobile(account)){
                ar.setSuccess(false);
                ar.setMessage("账号格式不正确！");
                ar.setErrorCode(CodeConstant.MOBILE_ERROR);
                return ar;
            }
            /*if(!Validator.isAccountByLetterAndNum(account)){
                ar.setSuccess(false);
                ar.setMessage("用户名应包含字母和数字哦，请重新设置！");
                ar.setErrorCode(CodeConstant.ERROE_PASSWORD_LETTER_NUM);
                return ar;
            }*/
            if(StringUtil.isEmpty(password)){
                ar.setSuccess(false);
                ar.setMessage("请输入密码！");
                ar.setErrorCode(CodeConstant.ERROE_PASSWORD_NULL);
                return ar;
            }
            
            if(password.length()<6||password.length()>16){
                ar.setSuccess(false);
                ar.setMessage("密码应为6-16位数，请重新设置");
                ar.setErrorCode(CodeConstant.ERROE_PASSWORD_6_16);
                return ar;
            }
            
            password = MD5Util.getMd5Code(password);
            //判断用户是否已存在
            if(userDetailsService.findIsExist(account,Constant.VERSION_NO)){
                ar.setSuccess(false);
                ar.setMessage("该账号已注册！");
                ar.setErrorCode(CodeConstant.ACCOUNT_EXISTS);
                return ar;
            }
            pd.put("account", account);
            pd.put("user_type", "7");//境外物流
            pd.put("mobile_phone", account);
            pd.put("user_name", account);
            pd.put("status", "1");//会员状态默认启用
            pd.put("password", password);
            pd.put("lastlogin_ip", InternetUtil.getRemoteAddr(request));
            pd.put("lastlogin_port", InternetUtil.getRemotePort(request));
            
            /*String jsonStr = HttpUtil.sendPostData("http://192.168.10.47:8332/get_new_key", "");
            String jsonStr = HttpUtil.sendPostData(""+ PocConstants.BlockChainURLTest+"/get_new_key", "");
            JSONObject jsonObj = JSONObject.parseObject(jsonStr);
            pd.put("public_key", jsonObj.getJSONObject("result").getString("PubKey"));
            pd.put("address", jsonObj.getJSONObject("result").getString("PubKey"));*/
            
            if(!userDetailsService.addUser(pd,Constant.VERSION_NO)){            
                ar.setSuccess(false);
                ar.setMessage("注册失败！");
                ar.setErrorCode(CodeConstant.REGISTER_FAIL);
                return ar;
            }
            
            PageData userInfo = userDetailsService.getUserInfoByAccount(account,Constant.VERSION_NO);
            String sessionId = RequestUtils.getRequestValue(CookieConstant.CSESSIONID,request);
            SessionUtil.setAttributeForUser(sessionId, JSON.toJSONString(userInfo));
            data.put("CSESSIONID", Base64.getBase64(sessionId));
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("注册成功！");
            return ar;
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setMessage("网络繁忙，请稍候重试！");
            ar.setErrorCode(CodeConstant.SYS_ERROR);
        }
        return ar;
    }
    
    /**
     * 访问系统首页
     */
    @LoginVerify
    @PostMapping("/index")
    @ApiOperation(nickname = "获取用户信息", value = "获取用户信息", notes = "获取用户信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "CSESSIONID", value = "会话token", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse index(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String,Object>();
        try {
            String sessionId = RequestUtils.getRequestValue(CookieConstant.CSESSIONID,request);
            String userstr = SessionUtil.getAttibuteForUser(sessionId);
            JSONObject user = JSONObject.parseObject(userstr);
            PageData userInfo = userDetailsService.getUserInfoByUserId(user.getInteger("user_id"), Constant.VERSION_NO);
            data.put("userInfo", userInfo);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
            return ar;
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setMessage("网络繁忙，请稍候重试！");
            ar.setErrorCode(CodeConstant.SYS_ERROR);
        }
        return ar;
    }
    
    /**
     * 进入首页后的默认页面
     * @return
     */
    @RequestMapping(value="/login_default")
    public String defaultPage(){
        return "system/admin/default";
    }
    
    /**
     * 用户注销
     * @param request
     * @return
     */
    @PostMapping(value="/logout")
    @ApiOperation(nickname = "退出登陆", value = "退出登陆", notes = "退出登陆！")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "CSESSIONID", value = "登陆token", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse logout(HttpServletRequest request)throws Exception{
        AjaxResponse ar = new AjaxResponse();
        SessionUtil.delAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
        ar.setSuccess(true);
        ar.setMessage("退出成功！");
        return ar; 
    }
    
    /**
     * @describe:忘记密码
     * @author: zhangchunming
     * @date: 2016年10月26日下午1:40:57
     * @param request
     * @param response
     * @return: AjaxResponse
     */
    @PostMapping("/forgetpwd")
    @ApiOperation(nickname = "忘记密码", value = "忘记密码，输入新密码", notes = "忘记密码，输入新密码！")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "account", value = "登陆账号", required = true, paramType = "query", dataType = "String"),
        @ApiImplicitParam(name = "password", value = "新密码", required = true, paramType = "query", dataType = "String"),
        @ApiImplicitParam(name = "cfPassWord", value = "确认密码", required = true, paramType = "query", dataType = "String"),
        @ApiImplicitParam(name = "vcode", value = "验证码", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse forgetpwd(HttpServletRequest request,HttpServletResponse response){
        AjaxResponse ar = new AjaxResponse();
        try {
            String account  =request.getParameter("account");
//            String phone  =request.getParameter("phone");
            String passWord  =request.getParameter("password");
            String cfPassWord  =request.getParameter("cfPassWord");
            String vcode  =request.getParameter("vcode");
            if(StringUtil.isEmpty(account)){
                ar.setSuccess(false);
                ar.setMessage("登陆账号不能为空，请重新输入");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            /*if(!Validator.isMobile(phone)){
                ar.setSuccess(false);
                ar.setMessage("手机号为11位数字，请重新输入");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }*/
            if(StringUtil.isEmpty(vcode.trim())){
                ar.setSuccess(false);
                ar.setMessage("验证码不能为空！");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            if(StringUtil.isEmpty(passWord)||StringUtil.isEmpty(cfPassWord)){
                ar.setSuccess(false);
                ar.setMessage("密码不能为空!");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            } 
            if(passWord.length()<6||passWord.length()>16){
                ar.setSuccess(false);
                ar.setMessage("密码应为6-16位数，请重新设置");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            /*if(!Validator.isPasswordByLetterAndNum(passWord)){
                ar.setSuccess(false);
                ar.setMessage("密码应包含字母和数字哦，请重新设置");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }*/

            if(!passWord.equals(cfPassWord)){
                ar.setSuccess(false);
                ar.setMessage("两次密码输入不一致，请重新输入！");
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                return ar;
            }
            if(!userLoginService.findIsExist(account, Constant.VERSION_NO)){
                ar.setSuccess(false);
                ar.setMessage("账号不存在，请重新输入！");
                ar.setErrorCode(CodeConstant.USER_NO_EXISTS);
                return ar;
            }
            PageData userInfo = userService.getPhoneByAccount(account, Constant.VERSION_NO);
            if(!"1".equals(userInfo.getString("user_type"))&&!"2".equals(userInfo.getString("user_type"))){//目前只支持普通会员和创业会员使用忘记密码功能
                ar.setSuccess(false);
                ar.setMessage("目前只有普通会员和创业会员才可以使用忘记密码功能哦！");
                ar.setErrorCode(CodeConstant.ERROR_DISABLE);
                return ar;
            }
            if(StringUtil.isEmpty(userInfo.getString("mobile_phone"))){
                ar.setSuccess(false);
                ar.setMessage("您的账号未绑定手机号，忘记密码功能无法使用，请重新注册！");
                ar.setErrorCode(CodeConstant.USERMOBILE_NOEXISTS);
                return ar;
            }
            //半小时之内的短信验证码有效
            String tVcode =sendVodeService.findVcodeByPhone(userInfo.getString("mobile_phone"), Constant.VERSION_NO); 
            if(tVcode ==null||!vcode.trim().equals(tVcode.trim())){
                ar.setSuccess(false);
                ar.setMessage("验证码输入不正确！");
                ar.setErrorCode(CodeConstant.ERROR_VCODE);
                return ar;
            }
            if(!userLoginService.modifyPwd(account, MD5Util.getMd5Code(passWord),Constant.VERSION_NO)){         
                ar.setSuccess(false);
                ar.setMessage("设置密码失败！");
                ar.setErrorCode(CodeConstant.UPDATE_FAIL);
                return ar;
            }
                ar.setSuccess(true);
                ar.setMessage("设置密码成功！");
                return ar;
            
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setMessage("网络繁忙，请稍候重试！");
            ar.setErrorCode(CodeConstant.SYS_ERROR);
        }
        return ar;
    }
    
    
    /**
     * @describe:跳往会员中兴
     * @author: zhangchunming
     * @date: 2017年4月26日下午3:03:06
     * @param request
     * @return: AjaxResponse
     */
    @LoginVerify
    @PostMapping("/toMemberCenter")
    @ApiOperation(nickname = "个人中心", value = "个人中心，查询用户信息", notes = "个人中心，查询用户信息！")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "CSESSIONID", value = "会话token", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse toMemberCenter(HttpServletRequest request){
        logBefore(logger,"去往用户中兴");
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String,Object>();
        try {
            PageData pd = new PageData();
            pd  = this.getPageData();
            //从session中查询用户信息
            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);
            Integer user_id = user.getInteger("id");
            
            //获取用户信息
            pd.put("user_id", user_id);
            PageData userInfo = userDetailsService.getUserInfoByUserId(user_id, Constant.VERSION_NO);
            data.put("userInfo", userInfo);
                
            //商城订单列表
            
            PageData oneSupplier = null;
            if ("4".equals(user.getString("user_type"))) {//供应商
                //根据user_id查询供应商信息
//                supplierList = shopOrderInfoService.getSupplierByUserId(String.valueOf(user.get("id")), Constant.VERSION_NO);
                oneSupplier = shopOrderInfoService.getOneSupplierByUserId(String.valueOf(user.get("id")), Constant.VERSION_NO);
                if (oneSupplier == null) {
                    logger.error("--------查询商城订单列表-----------根据user_id查找不到供应商信息！");
                    ar.setSuccess(false);
                    ar.setMessage("查找不到供应商信息！");
                    ar.setErrorCode(CodeConstant.NO_EXISTS);
                    return ar;
                }
                /*for(PageData supplier:supplierList){
                    supplierIdList.add((Integer)supplier.get("id"));
                }
                pd.put("supplierList", supplierList);*/
                pd.put("supplier_id", String.valueOf(oneSupplier.get("id")));
            } else {
                pd.put("user_id", String.valueOf(user.get("id")));
                pd.put("user_type", String.valueOf(user.getString("user_type")));
            }
            
            PageData orderPD = new PageData();
            List<PageData> shopOrderList = null;
            if("6".equals(user.getString("user_type"))||"7".equals(user.getString("user_type"))){//6-国内物流 7-境外物流
                //分页查询商城订单列表
                orderPD.put("user_type", user.getString("user_type"));
            }else if("4".equals(user.getString("user_type"))){//卖家
                //分页查询商城订单列表
                orderPD.put("supplier_id", String.valueOf(user.get("id")));
            }else if("1".equals(user.getString("user_type"))){//买家
                orderPD.put("user_id", String.valueOf(user.get("id")));
            }
            shopOrderList =  shopOrderInfoService.listShopOrderByPage(orderPD);//查询所有订单
            if (shopOrderList != null && shopOrderList.size()>0) {
                List<String> orderIdList = new ArrayList<String>();
                for (PageData shopOrder : shopOrderList) {
                    orderIdList.add(String.valueOf(shopOrder.get("order_id")));
                }
                PageData shopOrder = new PageData();
                if ("4".equals(user.getString("user_type"))) {//供应商
//                    shopOrder.put("supplierIdList", supplierIdList);
                    shopOrder.put("supplier_id", String.valueOf(oneSupplier.get("id")));
                } else {
                    shopOrder.put("user_id", String.valueOf(user.get("id")));
                }
                shopOrder.put("orderIdList", orderIdList);
                List<PageData> shopGoods = shopOrderInfoService.getGoodsByOrderId(shopOrder, Constant.VERSION_NO);

                if (shopGoods != null && shopGoods.size() > 0) {
                    for (PageData newShopOrder : shopOrderList) {
                        List<PageData> shopGoodsList = new ArrayList<PageData>();
                        for (PageData newShopGoods : shopGoods) {
                            if (newShopGoods.getString("shop_order_id").equals(String.valueOf(newShopOrder.get("order_id")))) {
                                shopGoodsList.add(newShopGoods);
                            }
                        }
                        newShopOrder.put("shopGoodsList", shopGoodsList);
                    }
                }
            }
            data.put("shopOrderList", shopOrderList);
            ar.setData(data);
            ar.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setMessage("网络繁忙，请稍候重试！");
            ar.setErrorCode(CodeConstant.SYS_ERROR);
        }
        logAfter(logger);
        return ar;
    }
    
    @LoginVerify
    @PostMapping("/getUserInfo")
    @ApiOperation(nickname = "获取用户基本信息", value = "获取用户基本信息", notes = "获取用户基本信息")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "CSESSIONID", value = "会话token", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse getUserInfo(HttpServletRequest request){
        logBefore(logger,"获取用户信息");
        AjaxResponse ar = new AjaxResponse();
        try {
            String userstr = SessionUtil.getAttibuteForUser(RequestUtils.getRequestValue(CookieConstant.CSESSIONID, request));
            JSONObject user = JSONObject.parseObject(userstr);
            PageData userInfo = userDetailsService.getUserInfoByUserId(user.getInteger("id"), Constant.VERSION_NO);
            ar.setSuccess(true);
            ar.setData(userInfo);
            ar.setMessage("获取用户信息成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setMessage("网络繁忙，请稍候重试！");
            ar.setErrorCode(CodeConstant.SYS_ERROR);
        }
        logAfter(logger);
        return ar;
    }
    
    
    public static void main(String[] args) {
      //初始  
        /*BigInteger num = new BigInteger("0");  
        num = num.setBit(2);  
        System.out.println(num);  
        num = num.setBit(1);  
        System.out.println(num);  
        System.out.println(num.testBit(2));  
        System.out.println(num.testBit(1));  
        System.out.println(num.testBit(3));  
        System.out.println("".length());
        String a = "KzopQgjVwAG2cqEHLWLJ9DKU5LpiUjcbREKoX3LbVgzPVC4S3Hynl".substring(0, 32);
        System.out.println(a.length());
        System.out.println(a.toString());
        
        System.out.println("KzopQgjVwAG2cqEHLWLJ9DKU5LpiUjcbREKoX3LbVgzPVC4S3Hynl");
        System.out.println(Base64.getBase64("KzopQgjVwAG2cqEHLWLJ9DKU5LpiUjcbREKoX3LbVgzPVC4S3Hynl"));
        System.out.println(Base64.getFromBase64("S3pvcFFnalZ3QUcyY3FFSExXTEo5REtVNUxwaVVqY2JSRUtvWDNMYlZnelBWQzRTM0h5bmw="));*/
        
        String base64 = Base64.getBase64("9fd50096398d4b428d57da0f4bffbb67");
        System.out.println("base64="+base64);
        
        char[] a = {'1','2'};
        System.out.println(a.toString());
        
    }
    
    public interface Clibrary extends Library { 

//        Dll INSTANCE = (Dll) Native.loadLibrary("btcsign", Dll.class);//加载动态库文件
        
            public Clibrary INSTANCE = (Clibrary) Native.loadLibrary("/data/lib/libbtcsign.so", Clibrary.class);
            //获得公钥和私钥,第一个参数密钥种子为可选项，如果有值，则长度为32
//            public int GetPriPubKey( String seeds, String prikey, String pubkey, String errmsg);
            public int GetPriPubKey( byte[] seeds, byte[] prikey, byte[] pubkey, byte[] errmsg);

            //获得数据的sign,输入私钥和数据，返回签名
//            public int GetDataSign( String prikey, String data, String sign, String errmsg);
            public int GetDataSign(byte[] prikey, byte[] data, byte[] sign, byte[] errmsg);
            public void InitCrypt();
            public void StopCrypt();
   }
   /* public interface Clibrary extends Library { 
        
        public Clibrary INSTANCE = (Clibrary) Native.loadLibrary("/data/lib/libtest_lib.so", Clibrary.class);
        int TestLib(int a,int b,char op);
    }*/
    /*public interface CLibrary extends Library {
        CLibrary INSTANCE = (CLibrary)
            Native.loadLibrary((Platform.isWindows() ? "msvcrt" : "c"),CLibrary.class);
        void printf(String format, Object... args);
    }

    public static void main(String[] args) {
        CLibrary.INSTANCE.printf("============================================Hello, World\n");
        for (int i=0;i < args.length;i++) {
            CLibrary.INSTANCE.printf("Argument %d: %s\n", i, args[i]);
        }
    }*/

}
