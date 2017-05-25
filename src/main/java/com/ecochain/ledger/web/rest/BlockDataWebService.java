package com.ecochain.ledger.web.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tk.mybatis.mapper.util.StringUtil;

import com.ecochain.ledger.base.BaseWebService;
import com.ecochain.ledger.constants.CodeConstant;
import com.ecochain.ledger.constants.Constant;
import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.service.SysGenCodeService;
import com.ecochain.ledger.util.AjaxResponse;
import com.ecochain.ledger.util.DateUtil;
import com.ecochain.ledger.util.HttpTool;
import com.ecochain.ledger.util.Logger;
/**
 * 账户控制类
 * @author zhangchunming
 */
@RestController
@RequestMapping(value = "/api/block")
@Api(value = "账户管理")
public class BlockDataWebService extends BaseWebService{
    
    private final Logger logger = Logger.getLogger(BlockDataWebService.class);
    
    @Autowired
    private SysGenCodeService sysGenCodeService;


    /**
     * @describe:获取最新的记录数据
     * @author: zhangchunming
     * @date: 2017年5月25日下午2:35:14
     * @param request
     * @return: AjaxResponse
     */
    @PostMapping("/getDataList")
    @ApiOperation(nickname = "获取最新的记录数据", value = "获取最新的记录数据", notes = "获取最新的记录数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "rows", value = "查询条数", required = false, paramType = "query", dataType = "String")
    })
    public AjaxResponse getDataList(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = this.getPageData();
        try {
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            String rows = "10";
            if(pd.getRows()!=null){
                rows = String.valueOf(pd.getRows());
            }
            String blockData = HttpTool.doPost(kql_url+"/GetDataList", rows);
            data.put("list", blockData);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }
    
    /**
     * @describe:查询区块列表
     * @author: zhangchunming
     * @date: 2017年5月25日下午2:37:37
     * @param request
     * @return: AjaxResponse
     */
    @PostMapping("/getBlockList")
    @ApiOperation(nickname = "获取最新的区块数据", value = "获取最新的区块数据", notes = "获取最新的区块数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "rows", value = "查询区块个数", required = false, paramType = "query", dataType = "String")
    })
    public AjaxResponse getBlockList(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = this.getPageData();
        try {
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            String rows = "10";
            if(pd.getRows()!=null){
                rows = String.valueOf(pd.getRows());
            }
            String blockData = HttpTool.doPost(kql_url+"/GetBlockList", rows);
            data.put("list", blockData);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }
    
    /**
     * @describe:按日期查询区块数据
     * @author: zhangchunming
     * @date: 2017年5月25日下午2:39:44
     * @param request
     * @return: AjaxResponse
     */
    @PostMapping("/getBlockByDate")
    @ApiOperation(nickname = "按日期查询区块数据", value = "按日期查询区块数据", notes = "按日期查询区块数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "date", value = "查询日期，请求样例：2017-5-25 12:30:10", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse getBlockByDate(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = this.getPageData();
        try {
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            String date = DateUtil.getCurrDateTime();
            if(pd.getString("date")!=null){
                date = pd.getString("date");
            }
            String blockData = HttpTool.doPost(kql_url+"/getBlockByDate", date);
            data.put("list", blockData);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }
    
    /**
     * @describe:根据hash值查询交易数据
     * @author: zhangchunming
     * @date: 2017年5月25日下午2:45:14
     * @param request
     * @return: AjaxResponse
     */
    @PostMapping("/getDataByHash")
    @ApiOperation(nickname = "根据hash值查询交易数据", value = "根据hash值查询交易数据", notes = "根据hash值查询交易数据")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "hash", value = "hash值，需带双引号", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse getDataByHash(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = this.getPageData();
        try {
            if(StringUtil.isEmpty(pd.getString("hash"))){
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                ar.setMessage("请输入hash值！");
                ar.setSuccess(false);
                return ar;
            }
            
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            String result = HttpTool.doPost(kql_url+"/getBlockByDate", pd.getString("hash"));
            data.put("result", result);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }
    /**
     * @describe:根据区块hash值查询区块高度
     * @author: zhangchunming
     * @date: 2017年5月25日下午2:55:23
     * @param request
     * @return: AjaxResponse
     */
    @PostMapping("/getBlockHeight")
    @ApiOperation(nickname = "根据区块hash值查询区块高度", value = "根据区块hash值查询区块高度", notes = "根据区块hash值查询区块高度")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "hash", value = "区块hash值，需带双引号", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse getBlockHeight(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = this.getPageData();
        try {
            if(StringUtil.isEmpty(pd.getString("hash"))){
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                ar.setMessage("请输入区块hash值！");
                ar.setSuccess(false);
                return ar;
            }
            
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            String result = HttpTool.doPost(kql_url+"/getBlockHeight", pd.getString("hash"));
            data.put("result", result);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }
    
    /**
     * @describe:根据区块高度获取区块的Hash
     * @author: zhangchunming
     * @date: 2017年5月25日下午2:57:45
     * @param request
     * @return: AjaxResponse
     */
    @PostMapping("/getBlockHashByHe")
    @ApiOperation(nickname = "根据区块高度获取区块的Hash", value = "根据区块高度获取区块的Hash", notes = "根据区块高度获取区块的Hash")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "height", value = "区块高度", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse getBlockHashByHe(HttpServletRequest request){
        AjaxResponse ar = new AjaxResponse();
        Map<String,Object> data = new HashMap<String, Object>();
        PageData pd = this.getPageData();
        try {
            if(StringUtil.isEmpty(pd.getString("height"))){
                ar.setErrorCode(CodeConstant.PARAM_ERROR);
                ar.setMessage("请输入区块高度！");
                ar.setSuccess(false);
                return ar;
            }
            
            String kql_url =null;
            List<PageData> codeList =sysGenCodeService.findByGroupCode("QKL_URL", Constant.VERSION_NO);
            for(PageData mapObj:codeList){
                if("QKL_URL".equals(mapObj.get("code_name"))){
                    kql_url = mapObj.get("code_value").toString();
                }
            }
            String result = HttpTool.doPost(kql_url+"/getBlockHashByHe", pd.getString("height"));
            data.put("result", result);
            ar.setData(data);
            ar.setSuccess(true);
            ar.setMessage("查询成功！");
        } catch (Exception e) {
            e.printStackTrace();
            ar.setSuccess(false);
            ar.setErrorCode(CodeConstant.SYS_ERROR);
            ar.setMessage("网络繁忙，请稍候重试！");
        }   
        return ar;
    }
}
