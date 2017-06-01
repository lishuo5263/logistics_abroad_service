package com.ecochain.ledger.web.rest;


import com.ecochain.ledger.base.BaseWebService;
import com.ecochain.ledger.constants.CodeConstant;
import com.ecochain.ledger.model.Page;
import com.ecochain.ledger.model.PageData;
import com.ecochain.ledger.model.ShopGoods;
import com.ecochain.ledger.service.ShopOrderLogisticsDetailService;
import com.ecochain.ledger.util.AjaxResponse;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by LiShuo on 2017/06/01.
 */
@RestController
@RequestMapping(value = "/api/rest/logistics")
public class shopOrderLogisticsDetailWebService extends BaseWebService {

    private Logger logger = Logger.getLogger(shopOrderLogisticsDetailWebService.class);

    @Autowired
    private ShopOrderLogisticsDetailService shopOrderLogisticsDetailService;

    /**
     * 物流转货
     */
    @GetMapping("/transferLogistics")
    @ApiOperation(nickname = "myCartToGenerateOrder", value = "购物车跳转生成订单必要信息查询", notes = "购物车跳转生成订单必要信息查询！！")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type", value = "查询类型", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "num", value = "购买个数", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "goodsId", value = "商品ID", required = true, paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "isPromote", value = "商品订单类型 0普通订单 1秒杀订单 2人民币订单", required = true, paramType = "query", dataType = "String")
    })
    public AjaxResponse transferLogistics(HttpServletRequest request, Page page){
        PageData pd = new PageData();
        pd = this.getPageData();
        page.setPd(pd);
        AjaxResponse ar= new AjaxResponse();
        Map<String, Object> map=new HashMap<String, Object>();
        List<ShopGoods> listt =new ArrayList<ShopGoods>();
        String userId=null;
        String goodsId=null;
        String num=null;
        String[] id=null;
        try{
       /* String type=(String)pd.get("type");
        String isPromote=(String)pd.get("isPromote");
        if(type.equals("directBuy")){
            userId =(String)pd.get("userId");
            goodsId=(String)pd.get("goodsId");
            num=(String)pd.get("num");
            map.put("num",num);
        }else if(type.equals("generateOrder")){
            userId =(String)pd.get("userId");
            id=pd.getString("id").split(",");
            num=(String)pd.get("num");
            map.put("userId",userId);
            map.put("num",num);
        }else if(type.equals("RMBBuy")){  //购买人民币种类商品
            goodsId=(String)pd.get("goodsId");
            map.put("toGenerateOrderInfo",this.shopGoodsService.queryRMBGoodsDetailInfoByGoodsId(goodsId));
            return fastReturn(map,true,"查询立即购买人民币商品信息成功！",CodeConstant.SC_OK);
        }
        logger.info("购物车跳转生成订单必要信息查询");
            if(!StringUtil.isNotEmpty(userId)){
                return fastReturn(null,false,"接口参数userId异常，购物车跳转生成订单必要信息查询失败！",CodeConstant.PARAM_ERROR);
            }else if(StringUtil.isNotEmpty(goodsId) && StringUtil.isNotEmpty(num)){   //立即购买信息查询  PHP秒杀下单
                listt=this.shopCartService.queryGoodsDetailInfoByGoodsId(goodsId,isPromote);
                if(listt !=null && listt.size() >0){
                    listt.get(0).setNum(num);
                    map.put("toGenerateOrderInfo",listt);
                    map.remove("userId");
                    map.remove("list");
                    map.remove("num");
                    return fastReturn(map,true,"查询立即购买商品信息成功！",CodeConstant.SC_OK);
                }else{
                    return fastReturn(map,true,"接口查询立即购买商品信息失败，查询不到相关信息！",CodeConstant.SC_OK);
                }
            }else if(id !=null && id.length >0) {
                    List<String> list = Arrays.asList(id);
                    map.put("list",list);
                    List list2=this.shopCartService.queryCartAllGoodsID(map);
                    if(list.size() ==list2.size()){ //去查询跳转生成订单页面数据查询
                        list=this.shopCartService.myCartToGenerateOrder(map);
                        map.put("toGenerateOrderInfo",list);
                        map.remove("userId");
                        map.remove("list");
                        map.remove("num");
                        ar=fastReturn(map,true,"购物车跳转生成订单必要信息查询成功！",CodeConstant.SC_OK);
                    }else{
                        return fastReturn(null,false,"接口参数id与数据库不匹配，购物车跳转生成订单必要信息查询失败！",CodeConstant.SC_OK);
                    }
            }else{
                ar=fastReturn(null,false,"接口参数id异常，购物车跳转生成订单必要信息查询失败！",CodeConstant.PARAM_ERROR);
            }*/
        } catch (ClassCastException e){
            logger.debug(e.toString(), e);
            ar=fastReturn(null,false,"接口参数类型异常，购物车跳转生成订单必要信息查询失败！", CodeConstant.PARAM_ERROR);
        }catch(NullPointerException e){
            logger.debug(e.toString(), e);
            ar=fastReturn(null,false,"接口参数异常，购物车跳转生成订单必要信息查询失败！", CodeConstant.PARAM_ERROR);
        }catch(Exception e){
            logger.debug(e.toString(), e);
            ar=fastReturn(null,false,"系统异常，购物车跳转生成订单必要信息查询失败！", CodeConstant.SYS_ERROR);
        }
        return ar;
    }
}
