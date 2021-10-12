package Vignette.Branching;
import ConstantVariables.ConstantVariables;
import Vignette.Page.VignettePage;
public class BranchingImpl implements BranchingInterface {
    VignettePage page;

    public  BranchingImpl(VignettePage page){
        this.page = page;
    }


    @Override
    public String noBranching() {
        return ("questionType = \""+ ConstantVariables.QUESTIONTYPE_NOBRACNH +"\";\n"+
                "branchToPage = {\n"+
                " \"default\": \""+page.getConnectedTo()+"\"\n};\n");
    }

    @Override
    public String branchingRadio(String pageAnswers) {
        return( "questionType =\""+ConstantVariables.QUESTIONTYPE_BRACNHRADIO +"\";\n"+
                "branchToPage="+ pageAnswers +"\n;");
    }

    @Override
    public void branchingCheckBox() {

    }
}
