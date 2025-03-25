package com.igenesys.model;

public class CombineMapQueryModel {
    private ResultQueryModel resultQueryStructureModel, resultQueryhohModel,
            resultQueryMemberModel;

    public CombineMapQueryModel(ResultQueryModel resultQueryWardModel, ResultQueryModel resultQueryULBModel, ResultQueryModel resultQueryDistrictModel) {
        this.resultQueryStructureModel = resultQueryWardModel;
        this.resultQueryhohModel = resultQueryULBModel;
        this.resultQueryMemberModel = resultQueryDistrictModel;
    }

    public ResultQueryModel getResultQueryStructureModel() {
        return resultQueryStructureModel;
    }

    public void setResultQueryStructureModel(ResultQueryModel resultQueryStructureModel) {
        this.resultQueryStructureModel = resultQueryStructureModel;
    }

    public ResultQueryModel getResultQueryhohModel() {
        return resultQueryhohModel;
    }

    public void setResultQueryhohModel(ResultQueryModel resultQueryhohModel) {
        this.resultQueryhohModel = resultQueryhohModel;
    }

    public ResultQueryModel getResultQueryMemberModel() {
        return resultQueryMemberModel;
    }

    public void setResultQueryMemberModel(ResultQueryModel resultQueryMemberModel) {
        this.resultQueryMemberModel = resultQueryMemberModel;
    }
}
