package org.campusforum.backend;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;


@SpringBootTest
class BackendApplicationTests {
    @Resource
    RestTemplate restTemplate;
    @Test
    void contextLoads() {
        String accessToken = getToken();
        String text = """
                人民网北京6月11日电 （记者许维娜）“端午节当天，景区举办了‘旱地龙舟拔河赛’‘趣味投壶’这类充满唐风唐韵的活动，特别热闹，特别震撼，全家非常开心，感受到了浓浓的节日氛围。”10日，游客何先生在游览西安两天后，与记者分享了他对这座城市的深刻印象。          
                让何先生一家印象深刻的还有“寻梦大唐”巡游活动，其中贺知章、上官婉儿等历史名人的生动再现，引领着民众们深入领略大唐文化的精髓。     
                有专家表示，“新中式”旅游结合现代审美和技术，展现各地人文风情魅力，深受游客喜爱。这不仅促进了中华优秀传统文化的创新发展，还催生了新的消费增长点，为经济高质量发展提供新动能、新优势。                      
                当前，周边短途游或 “家门口”旅行又开始“香”了。
                在热门博物馆参观文物新展，感受历史底蕴；漫步古城古镇，欣赏传统建筑；体验非遗集市，了解传统工艺，体会匠人精神……记者梳理发现，今年端午节假期，全国多地推出特色活动、新型消费场景和惠民措施，丰富群众的文化生活，推动文旅融合发展。                  
                据文化和旅游部数据中心测算，2024年端午节假期，全国国内旅游出游合计1.1亿人次，同比增长6.3%；国内游客出游总花费403.5亿元，同比增长8.1%。
                文旅市场各项数据增势喜人，彰显消费信心的回升，更折射出中国经济强大韧性和潜力。             
                以北京为例，记者从北京市文化和旅游局获悉，今年端午节假期，北京旅游市场依旧热度不减，全市共接待游客779.2万人次，旅游消费100.9亿元。其中，长城、民俗、京郊游等成为假期出游新热点。             
                多个平台也相继发布了端午节假期旅游数据。来自携程方面数据显示，国内游热门目的地前十分别是北京、上海、广州、杭州、成都、南京、深圳、重庆、西安和武汉。一线、新一线城市依旧占据主位。
                随着《我的阿勒泰》的火热播出，新疆成为自驾游炙手可热的地点，端午小长假乌鲁木齐租车订单翻番，伊宁市增长140%。阿坝藏族羌族自治州凭借相似新疆的风貌成为阿勒泰“平替”，在端午节假期迎来了114%的旅游订单增长。     
                传统节假日激发了人们对国风文化、特色民俗的探索欲望。携程研究院专家表示，一种更为日常、更为轻松惬意的“新烟火主义”旅游正悄然兴起，深刻影响着公众的旅行观念，逐渐形成一种日常化、休闲化的“家门口”旅行新模式。比如，连续三场“国粹芳华”京剧专场演出在天津举行，吸引了许多市民及游客感受国粹魅力 
                居民对端午节传统习俗的热爱也体现在出行中。
                据T3出行大数据显示，用户出行订单增速Top10城市分别为菏泽、亳州、河源、龙岩、惠州、丽水、泸州、郴州、韶关、潮州，分别增长347%、302%、264%、211%、209%、198%、192%、188%、173%、171%。其中，广东、福建、湖南、浙江等龙舟赛事热门城市占据了七成，不少游客愿意为一睹“龙舟竞渡”风采而奔赴一座城。              
                以“2小时高铁圈”为代表的周边短途游引领假日风潮。交通运输部发布数据显示，6月8日，全社会跨区域人员流动量超2.27亿人次，环比增长20.9%。其中铁路客运量达到1690万人次。
                结合出行用户订单分布数据来看，T3出行相关负责人表示：“恰逢暑期将要临近，不少家庭会选择历史文化丰富、游乐设施多元的一线城市亲子游。”
                业内人士认为，随着端午节假期的落幕，全国各地旅游市场经历短暂热潮后，将逐步回归稳定态势。各地精心策划、推出的丰富多元的文化和旅游活动，不仅让广大游客尽享了难忘的假期时光，也为地方经济的蓬勃发展注入了强劲动力。
                """;
        System.out.println(getResult(accessToken, text));
    }
    String getToken(){
        String appKey = "r9VtVl4dAAZul6aWQz6QVNSZ";
        String secretKey = "ELUdJJaMXNluNrpO0Gs8YPgw1oKTID8S";
        String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id="+ appKey + "&client_secret=" + secretKey;
        JSONObject jsonObject = restTemplate.getForObject(url,JSONObject.class);
        if (jsonObject != null) {
            return jsonObject.getString("access_token");
        }
        return null;
    }
    String getResult(String token,String text){
        String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/ernie-lite-8k?access_token="+token;
        String tag = "对下面的内容生成50字以内合乎汉语语法的摘要";
        JSONObject jsonObject = new JSONObject();
        JSONArray messages = new JSONArray();
        JSONObject body = new JSONObject();
        body.put("role","user");
        body.put("content",text + tag);
        messages.add(body);
        jsonObject.put("messages",messages);
        JSONObject result = restTemplate.postForObject(url, jsonObject, JSONObject.class);
        if (result != null) {
            return result.getString("result");
        } else {
            return null;
        }
    }
}
