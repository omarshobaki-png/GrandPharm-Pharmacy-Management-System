package Tables;

// small data carrier for chart rows — every chart query returns a list of these
// (a label like "Branch A" and a numeric value like 1240.50)
public class ReportRow {

    private String label;
    private double value;
    
    public ReportRow(String label, double value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}