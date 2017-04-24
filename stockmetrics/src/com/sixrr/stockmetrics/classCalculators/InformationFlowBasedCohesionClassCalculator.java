/*
 * Copyright 2005-2017 Sixth and Red River Software, Bas Leijdekkers
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.sixrr.stockmetrics.classCalculators;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiMethod;
import com.sixrr.metrics.utils.BucketedCount;

/**
 * @author Aleksandr Chudov.
 */
public class InformationFlowBasedCohesionClassCalculator extends ClassCalculator {
    private final BucketedCount<PsiClass> metrics = new BucketedCount<PsiClass>();

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    @Override
    public void endMetricsRun() {
        for (final PsiClass aClass : metrics.getBuckets()) {
            postMetric(aClass, metrics.getBucketValue(aClass));
        }
        super.endMetricsRun();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        @Override
        public void visitMethod(PsiMethod method) {
            super.visitMethod(method);
            if (method.getContainingClass() == null) {
                return;
            }
            metrics.incrementBucketValue(method.getContainingClass(), method.getParameterList().getParametersCount());
        }
    }
}