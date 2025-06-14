package com.example.CallApiAngular.Service;

import com.example.CallApiAngular.Repository.OptionRepository;
import com.example.CallApiAngular.entity.Option;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class OptionService {

    @Autowired
    private OptionRepository optionRepository;


    public List<Option> getAllOptions() {
        return optionRepository.findAll();
    }

    public Option getOptionByHref(String href) {
        return optionRepository.findById(href).orElse(null);
    }

    public Option createOption(Option option) {
        return optionRepository.save(option);
    }

    public Option updateOption(String href, Option option) {
        Optional<Option> existingOpt = optionRepository.findById(href);
        if (existingOpt.isPresent()) {
            Option existing = existingOpt.get();
            existing.setname(option.getname());
            existing.sethref(option.gethref());
            existing.setGroup(option.getGroup());
            return optionRepository.save(existing);
        }
        return null;
    }

    public void deleteOption(String href) {
        optionRepository.deleteById(href);
    }
}
