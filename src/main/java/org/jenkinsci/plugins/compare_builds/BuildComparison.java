package org.jenkinsci.plugins.compare_builds;

import hudson.model.*;
import jenkins.model.Jenkins;

import java.io.IOException;

public class BuildComparison implements Action {
    private final Run target;

    public BuildComparison(Run target) {
        this.target = target;
    }

    /**
     * Gets the file name of the icon.
     *
     * @return If just a file name (like "abc.gif") is returned, it will be
     *         interpreted as a file name inside <tt>/images/24x24</tt>.
     *         This is useful for using one of the stock images.
     *         <p/>
     *         If an absolute file name that starts from '/' is returned (like
     *         "/plugin/foo/abc.gif'), then it will be interpreted as a path
     *         from the context root of Jenkins. This is useful to pick up
     *         image files from a plugin.
     *         <p/>
     *         Finally, return null to hide it from the task list. This is normally not very useful,
     *         but this can be used for actions that only contribute <tt>floatBox.jelly</tt>
     *         and no task list item. The other case where this is useful is
     *         to avoid showing links that require a privilege when the user is anonymous.
     * @see hudson.Functions#isAnonymous()
     * @see hudson.Functions#getIconFilePath(hudson.model.Action)
     */
    public String getIconFileName() {
        return "abc.gif";
    }

    /**
     * Gets the string to be displayed.
     * <p/>
     * The convention is to capitalize the first letter of each word,
     * such as "Test Result".
     */
    public String getDisplayName() {
        return "Compare";
    }

    /**
     * Gets the URL path name.
     * <p/>
     * <p>tions
     * For example, if this method returns "xyz", and if the parent object
     * (that this action is associated with) is bound to /foo/bar/zot,
     * then this action object will be exposed to /foo/bar/zot/xyz.
     * <p/>
     * <p/>
     * This method should return a string that's unique among other {@link hudson.model.Action}s.
     * <p/>
     * <p/>
     * The returned string can be an absolute URL, like "http://www.sun.com/",
     * which is useful for directly connecting to external systems.
     * <p/>
     * <p/>
     * If the returned string starts with '/', like '/foo', then it's assumed to be
     * relative to the context path of the Jenkins webapp.
     *
     * @return null if this action object doesn't need to be bound to web
     *         (when you do that, be sure to also return null from {@link #getIconFileName()}.
     * @see hudson.Functions#getActionUrl(String, hudson.model.Action)
     */
    public String getUrlName() {
        return "comparedTo";
    }

    public Project getJob(String name) throws IOException {
        return new ComparingProject(Jenkins.getInstance().getItemGroup(), name, (Project)Jenkins.getInstance().getItem(name));
    }

    private class ComparingBuild extends Build<ComparingProject, ComparingBuild> {

        private final AbstractBuild build;

        public ComparingBuild(ComparingProject project, AbstractBuild build) throws IOException {
            super(project);
            this.build = build;
        }

        /** {@inheritDoc} */
        @Override
        public long getDuration() {
            return build.getDuration() - BuildComparison.this.target.getDuration();
        }

        /** {@inheritDoc} */
        @Override
        public BallColor getIconColor() {
            return BallColor.NOTBUILT;
        }

        /** {@inheritDoc} */
        @Override
        public boolean isBuilding() {
            return false;
        }

        @Override
        public String getFullDisplayName() {
            return "Comparison of " +build.getFullDisplayName() +", relative to "+BuildComparison.this.target.getFullDisplayName();
        }
    }

    private class ComparingProject extends Project<ComparingProject, ComparingBuild> implements TopLevelItem{

        private final Project project;


        public ComparingProject(ItemGroup parent, String name, Project project) {
            super(parent, name);
            this.project = project;
        }

        /** {@inheritDoc} */
        @Override
        protected Class<ComparingBuild> getBuildClass() {
            return ComparingBuild.class;
        }

        /** {@inheritDoc} */
        @Override
        public ComparingBuild getBuildByNumber(int n) {
            try{
            return new ComparingBuild(this, project.getBuildByNumber(n));
            }catch (IOException e){
               return null;
            }
        }

        /**
         * @see hudson.model.Describable#getDescriptor()
         */
        public TopLevelItemDescriptor getDescriptor() {
            return null;
        }
    }
}
