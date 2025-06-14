package com.example.CallApiAngular.Service;

import com.example.CallApiAngular.entity.Group;
import com.example.CallApiAngular.Repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GroupService {

    private final GroupRepository groupRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @EntityGraph(attributePaths = "banners")
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupById(Long id) {
        return groupRepository.findById(id).orElse(null);
    }

    public Group createGroup(Group group) {
        return groupRepository.save(group);
    }

    public Group updateGroup(Long id, Group group) {
        Optional<Group> optionalGroup = groupRepository.findById(id);
        if (optionalGroup.isPresent()) {
            Group existing = optionalGroup.get();
            existing.setName(group.getName());
            existing.setUrl(group.getUrl());
            return groupRepository.save(existing);
        }
        return null;
    }

    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }
}
