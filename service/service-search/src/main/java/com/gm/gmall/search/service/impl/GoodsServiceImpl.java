package com.gm.gmall.search.service.impl;

import com.gm.gmall.model.list.*;
import com.gm.gmall.search.repository.GoodsRepository;
import com.gm.gmall.search.service.GoodsService;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author gym
 * @create 2022/9/5 0005 10:18
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    GoodsRepository goodsRepository;
    @Autowired
    ElasticsearchRestTemplate restTemplate;
    @Autowired
    ThreadPoolExecutor executor;


    @Override
    public void save(Goods goods) {
        goodsRepository.save(goods);
    }

    @Override
    public void delete(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    public SearchResponseVo search(SearchParam searchParam) {
        SearchResponseVo responseVo = new SearchResponseVo();
        NativeSearchQueryBuilder queryBuilder = buildQuery(searchParam);
        //??????????????????
        SearchHits<Goods> searchHits = restTemplate.search(queryBuilder.build(), Goods.class, IndexCoordinates.of("goods"));
        //??????vo
        //???????????????
        //????????????????????????
        if (!StringUtils.isEmpty(searchParam.getTrademark())) {
            String[] split = searchParam.getTrademark().split(":");
            responseVo.setTrademarkParam("??????:" + split[1]);
        }
        //.???????????????????????????
        if (searchParam.getProps() != null && searchParam.getProps().length > 0) {
            String[] props = searchParam.getProps();
            List<SearchAttr> list = new ArrayList<>();
            for (String prop : props) {
                SearchAttr searchAttr = new SearchAttr();
                String[] split = prop.split(":");
                searchAttr.setAttrId(Long.parseLong(split[0]));
                searchAttr.setAttrName(split[2]);
                searchAttr.setAttrValue(split[1]);
                list.add(searchAttr);
            }
            responseVo.setPropsParamList(list);
        }
        //??????????????????
        List<SearchResponseTmVo> tmVoList = getTradeMark(searchHits);
        responseVo.setTrademarkList(tmVoList);
        //??????????????????
        List<SearchResponseAttrVo> attrVo = getAttrVo(searchHits);
        responseVo.setAttrsList(attrVo);
        //??????????????????
        if (!StringUtils.isEmpty(searchParam.getOrder())) {
            String[] split = searchParam.getOrder().split(":");
            OrderMapVo orderMapVo = new OrderMapVo();
            orderMapVo.setType(split[0]);
            orderMapVo.setSort(split[1]);
            responseVo.setOrderMap(orderMapVo);
        }
        //????????????
        List<SearchHit<Goods>> hits = searchHits.getSearchHits();
        List<Goods> goodsList = new ArrayList<>();
        for (SearchHit<Goods> hit : hits) {
            Goods goods = hit.getContent();
            //????????????????????????????????????????????????????????????????????????
            if (!StringUtils.isEmpty(searchParam.getKeyword())) {
                goods.setTitle(hit.getHighlightField("title").get(0));
            }
            goodsList.add(goods);
        }
        responseVo.setGoodsList(goodsList);
        //??????
        responseVo.setPageNo(searchParam.getPageNo());
        //?????????
        long totalHits = searchHits.getTotalHits();
        long total = totalHits % searchParam.getPageSize() == 0 ? totalHits / searchParam.getPageSize() : (totalHits /searchParam.getPageSize() + 1);
        responseVo.setTotalPages(total);
        responseVo.setPageSize(searchParam.getPageSize());
        //??????url
        String url = setUrl(searchParam);
        responseVo.setUrlParam(url);
        return responseVo;
    }

    private String setUrl(SearchParam searchParam) {
        //?????????????????????URL
        StringBuilder stringBuilder=new StringBuilder("list.html?");
        if (searchParam.getCategory1Id()!=null){
            stringBuilder.append("&category1Id="+ searchParam.getCategory1Id());
        }
        if (searchParam.getCategory2Id()!=null){
            stringBuilder.append("&category2Id="+ searchParam.getCategory2Id());
        }
        if (searchParam.getCategory3Id()!=null){
            stringBuilder.append("&category3Id="+ searchParam.getCategory3Id());
        }
        //?????????????????????????????????trademark=2:??????
        if (!StringUtils.isEmpty(searchParam.getTrademark())){
            stringBuilder.append("&trademark="+ searchParam.getTrademark());
        }
        //?????????????????????
        if (!StringUtils.isEmpty(searchParam.getKeyword())){
            stringBuilder.append("&keyword="+ searchParam.getKeyword());
        }
        //????????????props=23:4G:????????????
        if (searchParam.getProps()!=null && searchParam.getProps().length>0){
            //??????????????????id??????????????????nested?????????
            String[] props = searchParam.getProps();
            for (String prop : props) {
                stringBuilder.append("&props="+prop);
            }
        }
        return stringBuilder.toString();
    }

    private List<SearchResponseAttrVo> getAttrVo(SearchHits<Goods> searchHits) {
        // ??????????????????
        List<SearchResponseAttrVo> attrVoList = new ArrayList<>();
        //??????nested???
        ParsedNested attr = searchHits.getAggregations().get("attr");
        //???????????????attrID
        ParsedLongTerms attrIdAgg = attr.getAggregations().get("attrId");
        attrIdAgg.getBuckets().forEach(attrId->{
            SearchResponseAttrVo searchResponseAttrVo=new SearchResponseAttrVo();
            long id = attrId.getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(id);
            //???????????????attrName
            ParsedStringTerms attrNameAgg = attrId.getAggregations().get("attrName");
            String name = attrNameAgg.getBuckets().get(0).getKeyAsString();
            searchResponseAttrVo.setAttrName(name);
            //??????????????????attrValue
            ParsedStringTerms attrValueAgg = attrId.getAggregations().get("attrValue");
            List<String> attrValueList=new ArrayList<>();
            attrValueAgg.getBuckets().forEach(attrValue->{
                String asString = attrValue.getKeyAsString();
                attrValueList.add(asString);
                searchResponseAttrVo.setAttrValueList(attrValueList);
            });
            attrVoList.add(searchResponseAttrVo);
        });

        return attrVoList;
    }

    private List<SearchResponseTmVo> getTradeMark(SearchHits<Goods> searchHits) {
        // ??????????????????
        List<SearchResponseTmVo> tmVoList = new ArrayList<>();
        ParsedLongTerms aggregation = searchHits.getAggregations().get("tmId");
        aggregation.getBuckets().forEach(bucket -> {
            Long key = bucket.getKeyAsNumber().longValue();
            SearchResponseTmVo responseTmVo = new SearchResponseTmVo();
            responseTmVo.setTmId(key);
            ParsedStringTerms tmName = bucket.getAggregations().get("tmName");
            String asString = tmName.getBuckets().get(0).getKeyAsString();
            responseTmVo.setTmName(asString);
            ParsedStringTerms tmLogoUrl = bucket.getAggregations().get("tmLogoUrl");
            String name = tmLogoUrl.getBuckets().get(0).getKeyAsString();
            responseTmVo.setTmLogoUrl(name);
            tmVoList.add(responseTmVo);
        });
       return tmVoList;
    }

    private NativeSearchQueryBuilder buildQuery(SearchParam searchParam) {
        //???????????????????????????
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();
        //????????????boolQuery
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        //????????????????????????????????????????????????????????????
        if (searchParam.getCategory1Id() != null) {
            query.must(QueryBuilders.termQuery("category1Id", searchParam.getCategory1Id()));
//            stringBuilder.append("&category1Id" + searchParam.getCategory1Id());
        }
        if (searchParam.getCategory2Id() != null) {
            query.must(QueryBuilders.termQuery("category2Id", searchParam.getCategory2Id()));
//            stringBuilder.append("&category2Id" + searchParam.getCategory2Id());

        }
        if (searchParam.getCategory3Id() != null) {
            query.must(QueryBuilders.termQuery("category3Id", searchParam.getCategory3Id()));
//            stringBuilder.append("&category3Id" + searchParam.getCategory3Id());

        }
        //?????????????????????????????????trademark=2:??????
        if (!StringUtils.isEmpty(searchParam.getTrademark())) {
            long tmId = Long.parseLong(searchParam.getTrademark().split(":")[0]);
            query.must(QueryBuilders.termQuery("tmId", tmId));
//            stringBuilder.append("&trademark" + searchParam.getTrademark());

        }
        //?????????????????????
        if (!StringUtils.isEmpty(searchParam.getKeyword())) {
            query.must(QueryBuilders.matchQuery("title", searchParam.getKeyword()));
//            stringBuilder.append("&keyword" + searchParam.getKeyword());

        }
        //????????????props=23:4G:????????????
        if (searchParam.getProps() != null && searchParam.getProps().length > 0) {
            //??????????????????id??????????????????nested?????????
            String[] props = searchParam.getProps();
            for (String prop : props) {
//                stringBuilder.append("&props" + prop);
                long attrId = Long.parseLong(prop.split(":")[0]);
                String attrValue = prop.split(":")[1];
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                boolQuery.must(QueryBuilders.matchQuery("attrs.attrId", attrId));
                boolQuery.must(QueryBuilders.matchQuery("attrs.attrValue", attrValue));
                NestedQueryBuilder nestedQueryBuilder = new NestedQueryBuilder("attrs", boolQuery, ScoreMode.None);
                query.must(nestedQueryBuilder);
            }
        }
        searchQueryBuilder.withQuery(query);
        //?????????????????????????????????
        TermsAggregationBuilder tmId = AggregationBuilders.terms("tmId").field("tmId").size(100);
        tmId.subAggregation(AggregationBuilders.terms("tmName").field("tmName")).size(100);
        tmId.subAggregation(AggregationBuilders.terms("tmLogoUrl").field("tmLogoUrl")).size(100);
        searchQueryBuilder.addAggregation(tmId);
        //??????????????????
        NestedAggregationBuilder nested = AggregationBuilders.nested("attr", "attrs");
        TermsAggregationBuilder attrId1 = AggregationBuilders.terms("attrId").field("attrs.attrId").size(100);
        attrId1.subAggregation(AggregationBuilders.terms("attrName").field("attrs.attrName").size(100));
        attrId1.subAggregation(AggregationBuilders.terms("attrValue").field("attrs.attrValue").size(100));
        nested.subAggregation(attrId1);
        searchQueryBuilder.addAggregation(nested);
        //??????order=1:desc  ???????????????????????????
        String[] split = searchParam.getOrder().split(":");
        String orderBy = "";
        switch (split[0]) {
            case "2":
                orderBy = "price";
                break;
            case "3":
                orderBy = "createTime";
                break;
            default:
                orderBy = "hotScore";
        }
        FieldSortBuilder sortBuilder = SortBuilders.fieldSort(orderBy);
        if (split[1].equals("desc")) {
            sortBuilder.order(SortOrder.DESC);
        } else {
            sortBuilder.order(SortOrder.ASC);
        }

        searchQueryBuilder.withSort(sortBuilder);
        //??????
        searchQueryBuilder.withPageable(PageRequest.of(searchParam.getPageNo() - 1, searchParam.getPageSize()));
        //????????????
        searchQueryBuilder.withHighlightBuilder(new HighlightBuilder().
                field("title").preTags("<span style='color:red'>").postTags("</span>"));

        return searchQueryBuilder;
    }
}
