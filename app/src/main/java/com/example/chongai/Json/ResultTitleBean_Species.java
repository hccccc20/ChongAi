package com.example.chongai.Json;

import com.example.chongai.Activity.Species_recognition;

import java.util.List;

public class ResultTitleBean_Species {

    /*
    Content-Type: application/json;charset=UTF-8
    {
        "log_id": 7392482912853822863,
            "result": [{
        "score": "0.993811",
                "name": "叉角羚",
                "baike_info": {
            "baike_url": "http://baike.baidu.com/item/%E5%8F%89%E8%A7%92%E7%BE%9A/801703",
                    "description": "叉角羚(学名：Antilocapra americana)：在角的中部角鞘有向前伸的分枝，故名。体型中等，体长1-1.5米，尾长7.5-10厘米，肩高81-104厘米，成体重36-60千克，雌体比雄体小；背面为红褐色，颈部有黑色鬃毛，腹部和臀部为白色，颊面部和颈部两侧有黑色块斑；毛被下面为绒毛，上覆以粗糙、质脆的长毛，由于某些皮肤肌的作用，能使其毛被呈不同角度，以利于保暖或散热。植食。叉角羚奔跑速度非常快，最高时速达100千米。一次跳跃可达3.5-6米。善游泳。夏季组成小群活动，冬季则集结成上百只的大群。为寻找食物和水源，一年中常进行几次迁移。性机警，视觉敏锐，能看到数千米外的物体。遇险时，臀部的白色毛能立起，向同伴告警。分布于北美洲。"
        }
    },
        {
            "score": "0.000289439",
                "name": "印度羚"
        },
        {
            "score": "0.000186248",
                "name": "藏羚羊"
        },
        {
            "score": "0.000147176",
                "name": "跳羚"
        },
        {
            "score": "0.000134434",
                "name": "驯鹿"
        },
        {
            "score": "9.86555e-05",
                "name": "高鼻羚羊"
        }]
    }
    */
    public long log_id;
    //  public int result_num;
    public List<ResultBean_Species> result;


}
