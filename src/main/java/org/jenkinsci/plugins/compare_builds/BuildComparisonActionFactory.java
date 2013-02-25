package org.jenkinsci.plugins.compare_builds;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Run;
import hudson.model.TransientBuildActionFactory;

import java.util.Collection;
import java.util.Collections;

@Extension
public class BuildComparisonActionFactory extends TransientBuildActionFactory {
    /**
     * Creates actions for the given build.
     *
     * @param target for which the action objects are requested. Never null.
     * @return Can be empty but must not be null.
     */
    @Override
    public Collection<? extends Action> createFor(Run target) {
        return Collections.singleton(new BuildComparison(target));
    }
}
