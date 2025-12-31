package com.example.source_share.model;

public enum CategoryCode {
    COURSEWORK,  // 课程作业
    PROPOSAL,    // 开题报告
    MIDTERM,     // 中期考核
    THESIS,      // 毕业设计
    OTHERS;      // 综合资源
    
    // 只有根目录会有这个值，子文件通常是 null
}