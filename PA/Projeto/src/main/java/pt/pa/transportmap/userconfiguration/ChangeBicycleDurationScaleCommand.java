package pt.pa.transportmap.userconfiguration;

/**
 * Command to change the bicycle duration scale in the transport map
 */
public class ChangeBicycleDurationScaleCommand implements BicycleDurationScaleCommand {
    /** User configuration reference */
    private final UserConfiguration userConfiguration;
    /** New bicycle duration scale */
    private final double bicycleDurationScale;
    /** Old bicycle duration scale */
    private final double oldBicycleDurationScale;

    /**
     * Constructor for ChangeBicycleDurationScaleCommand
     * @param userConfiguration UserConfiguration the user configuration
     * @param bicycleDurationScale double the new bicycle duration scale
     * @throws IllegalArgumentException if user configuration is null
     */
    public ChangeBicycleDurationScaleCommand(UserConfiguration userConfiguration, double bicycleDurationScale) throws IllegalArgumentException {
        super();
        if(userConfiguration == null) {
            throw new IllegalArgumentException("User configuration cannot be null.");
        }
        if(bicycleDurationScale < 0.25 || bicycleDurationScale > 2.0) {
            throw new IllegalArgumentException("Bicycle duration scale must be between 0.25 and 2.");
        }
        this.userConfiguration = userConfiguration;
        this.bicycleDurationScale = bicycleDurationScale;
        this.oldBicycleDurationScale = userConfiguration.getBicycleDurationScale();
    }

    @Override
    public void execute() {
        userConfiguration.setBicycleDurationScale(bicycleDurationScale);
    }

    @Override
    public void undo() {
        userConfiguration.setBicycleDurationScale(oldBicycleDurationScale);
    }

    @Override
    public String toString() {
        return "Change bicycle duration scale to " + bicycleDurationScale;
    }
}
