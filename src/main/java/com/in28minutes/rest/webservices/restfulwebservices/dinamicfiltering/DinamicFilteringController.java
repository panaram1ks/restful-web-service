package com.in28minutes.rest.webservices.restfulwebservices.dinamicfiltering;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class DinamicFilteringController {

    @GetMapping("/filtering-dinamic")
    public MappingJacksonValue filtering() {
        // MappingJacksonValue
        SomeDinamicBean someDinamicBean = new SomeDinamicBean("value1", "value2", "value3");

        // filtering logic
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(someDinamicBean);
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("field1", "field3");
        FilterProvider filters = new SimpleFilterProvider().addFilter("idOfYourFilter", filter);
        mappingJacksonValue.setFilters(filters);
        //

        return mappingJacksonValue;
    }

    @GetMapping("/filtering-list-dinamic")
    public MappingJacksonValue filteringList() {
        List<SomeDinamicBean> list =
                Arrays.asList(new SomeDinamicBean("value1", "password1", "value3"), new SomeDinamicBean("value1", "password2", "value3"));

        // filtering logic
        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(list);
        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("field2", "field3");
        FilterProvider filters = new SimpleFilterProvider().addFilter("idOfYourFilter", filter);
        mappingJacksonValue.setFilters(filters);
        //

        return mappingJacksonValue;
    }

}
