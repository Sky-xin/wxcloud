package cn.youyanni.wxcloud.controller;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.Check;
import com.ecwid.consul.v1.health.model.HealthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Logger;

@RestController
public class ApiController {
    Logger logger = Logger.getLogger(ApiController.class.getName());
    @Autowired
    private ConsulClient consulClient;

    @RequestMapping(value = "/unregister/{id}", method = RequestMethod.POST)
    public String unregisterServiceAll(@PathVariable String id){
        List<HealthService> response = consulClient.getHealthServices(id , false ,null).getValue();
        for (HealthService service: response ) {
            service.getChecks().forEach( check -> {
                if(!check.getStatus().name().equals(Check.CheckStatus.PASSING.name())){
                    consulClient.agentServiceDeregister(check.getServiceId());
                    logger.info("unregister: " + check.getServiceId());
                }
            });
        }
        return  "unregister success";
    }
}
