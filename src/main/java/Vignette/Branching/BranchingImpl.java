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

        String text = "questionType = \""+ ConstantVariables.QUESTIONTYPE_NOBRACNH +"\";\n"+
                "branchToPage = {\n"+
                " \"default\": \""+page.getConnectedTo()+"\"\n};\n";
        return text;
    }

    @Override
    public String branchingRadio(String pageAnswers) {

        String text = "questionType =\""+ConstantVariables.QUESTIONTYPE_BRACNHRADIO +"\";\n"+
                "branchToPage="+ pageAnswers +"\n;";

        return text;

    }

    @Override
    public void branchingCheckBox() {

    }
}
