package com.tiji.center.controller;

import com.tiji.center.pojo.Task;
import com.tiji.center.pojo.Taskip;
import com.tiji.center.service.TaskService;
import com.tiji.center.service.TaskipService;
import com.tiji.center.service.TaskportService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * taskip控制器层
 *
 * @author 贰拾壹
 */
@RestController
@CrossOrigin
@RequestMapping("/taskip")
public class TaskipController {

    @Autowired
    private TaskipService taskipService;
    @Autowired
    private TaskportService taskportService;
    @Autowired
    private TaskService taskService;


    /**
     * 查询全部数据
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll() {
        return new Result(true, StatusCode.OK, "查询成功", taskipService.findAll());
    }

    /**
     * 根据ID查询
     *
     * @param id ID
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Result findById(@PathVariable String id) {
        return new Result(true, StatusCode.OK, "查询成功", taskipService.findById(id));
    }


    /**
     * 分页+多条件查询
     *
     * @param searchMap 查询条件封装
     * @param page      页码
     * @param size      页大小
     * @return 分页结果
     */
    @RequestMapping(value = "/search/{page}/{size}", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap, @PathVariable int page, @PathVariable int size) {
        Page<Taskip> pageList = taskipService.findSearch(searchMap, page, size);
        pageList.stream().parallel().forEach(taskip -> {
            String taskid = taskip.getTaskid();
            Task task = taskService.findById(taskid);
            if (!Objects.isNull(task)) {
                taskip.setTaskid(task.getName());
            }
        });
        return new Result(true, StatusCode.OK, "查询成功", new PageResult<Taskip>(pageList.getTotalElements(), pageList.getContent()));
    }

    /**
     * 根据条件查询
     *
     * @param searchMap
     * @return
     */
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public Result findSearch(@RequestBody Map searchMap) {
        return new Result(true, StatusCode.OK, "查询成功", taskipService.findSearch(searchMap));
    }

    /**
     * 增加
     *
     * @param taskip
     */
    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Taskip taskip) {
        taskipService.add(taskip);
        return new Result(true, StatusCode.OK, "增加成功");
    }

    /**
     * 修改
     *
     * @param taskip
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Result update(@RequestBody Taskip taskip, @PathVariable String id) {
        taskip.setId(id);
        taskipService.update(taskip);
        return new Result(true, StatusCode.OK, "修改成功");
    }

    /**
     * 删除
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public Result delete(@PathVariable String id) {
        taskipService.deleteById(id);
        //删除资产端口
        taskportService.deleteAllByTaskipid(id);
        return new Result(true, StatusCode.OK, "删除成功");
    }

    /**
     * 根据id数组查询
     *
     * @param ids
     * @return
     */
    @RequestMapping(value = "/ids", method = RequestMethod.POST)
    public Result findByAssetIpIds(@RequestBody String[] ids) {
        return new Result(true, StatusCode.OK, "查询成功", taskipService.findByIds(ids));
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @RequestMapping(value = "/deleteids", method = RequestMethod.POST)
    public Result deleteAllByIds(@RequestBody List<String> ids) {
        taskipService.deleteAllByIds(ids);
        ids.forEach(id -> {
            //删除资产端口
            taskportService.deleteAllByTaskipid(id);
        });
        return new Result(true, StatusCode.OK, "删除成功");
    }
}
