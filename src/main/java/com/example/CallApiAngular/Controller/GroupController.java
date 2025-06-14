package com.example.CallApiAngular.Controller;
import com.example.CallApiAngular.Service.GroupService;

import com.example.CallApiAngular.entity.Group;
import com.example.CallApiAngular.Repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "http://localhost:4200")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupRepository groupRepository;

    @GetMapping
    public ResponseEntity<List<Group>> getAllGroups() {
        List<Group> groups = groupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }

    @PostMapping
    public ResponseEntity<Group> createGroup(@RequestBody Group group) {
        Group savedGroup = groupRepository.save(group);
        return ResponseEntity.ok(savedGroup);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Group> updateGroup(@PathVariable String id, @RequestBody Group group) {
        Long longId = Long.parseLong(id);
        Group existingGroup = groupRepository.findById(longId).orElse(null);

        if (existingGroup == null) {
            return ResponseEntity.notFound().build();
        }

        existingGroup.setName(group.getName());
        existingGroup.setUrl(group.getUrl());

        Group updatedGroup = groupRepository.save(existingGroup);
        return ResponseEntity.ok(updatedGroup);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        if (!groupRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        groupRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
