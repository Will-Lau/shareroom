package example.shareroom.service;


import example.shareroom.Entity.Credit;
import example.shareroom.Entity.CreditExample;
import example.shareroom.Response.CreditResponse;
import example.shareroom.dao.CreditMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

;import static example.shareroom.UsefulUtils.DozerUtils.mapList;

@Service

public class Creditservice {

    @Autowired
    CreditMapper creditMapper;

    @Autowired
    Userservice userservice;

   public String createCredit(Credit credit){
       Credit temp = creditMapper.selectByPrimaryKey(credit.getaId());
       if(temp==null){
           creditMapper.insertSelective(credit);
            return "创建成功";
       }
       else return "已存在";
   }


   public List<Credit> getCredit(Credit credit) {
       CreditExample creditExample =new CreditExample();
       CreditExample.Criteria criteria=creditExample.createCriteria();

       if(credit.getaId()!=null) criteria.andAIdEqualTo(credit.getaId());
       if(credit.getUserId()!=null) criteria.andUserIdEqualTo(credit.getUserId());
       if(credit.getChanges()!=null) criteria.andChangesEqualTo(credit.getChanges());

       List<Credit> credits=creditMapper.selectByExample(creditExample);
       if(credits==null||credits.size()==0) return null;
       else return credits;

    
   }


    public String updateCredit(Credit credit){
        Credit temp=creditMapper.selectByPrimaryKey(credit.getaId());
        
        if(temp!=null) 
        {
            creditMapper.updateByPrimaryKeySelective(credit);
            return "操作完成";
        }
        else return "不存在";

    }


}