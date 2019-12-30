package com.yzchnb.dynamicbarvideogenerator.Entity.ConfigurationEntity;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Scope("prototype")
public class GeneratorConfiguration {
    private UserInputConfiguration userInputConfiguration;

    public UserInputConfiguration getUserInputConfiguration() {
        return userInputConfiguration;
    }

    public String getTitle() {
        return userInputConfiguration.getTitle();
    }
    public static GeneratorConfiguration from(UserInputConfiguration u){
        return new GeneratorConfiguration(u);
    }


    private GeneratorConfiguration(UserInputConfiguration userInputConfiguration){
        this.userInputConfiguration = userInputConfiguration;
    }
}
