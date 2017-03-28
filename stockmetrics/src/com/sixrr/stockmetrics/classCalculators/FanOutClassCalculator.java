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
import com.intellij.psi.PsiMethodCallExpression;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Aleksandr Chudov on 28.03.2017.
 */
public class FanOutClassCalculator extends ClassCalculator {
    private final HashMap<PsiClass, Integer> metrics = new HashMap<PsiClass, Integer>();
    private final Stack<PsiClass> classes = new Stack<PsiClass>();

    @Override
    public void endMetricsRun() {
        for (Map.Entry<PsiClass, Integer> e : metrics.entrySet()) {
            postMetric(e.getKey(), e.getValue());
        }
        super.endMetricsRun();
    }

    @Override
    protected PsiElementVisitor createVisitor() {
        return new Visitor();
    }

    private class Visitor extends JavaRecursiveElementVisitor {
        @Override
        public void visitClass(PsiClass aClass) {
            classes.push(aClass);
            if (!metrics.containsKey(aClass))
                metrics.put(aClass, 0);
            super.visitClass(aClass);
            classes.pop();
        }

        @Override
        public void visitMethodCallExpression(PsiMethodCallExpression expression) {
            final PsiClass aClass = expression.resolveMethod().getContainingClass();
            if (classes.peek().equals(aClass)) {
                return;
            }
            Integer metric = metrics.containsKey(aClass) ? metrics.get(aClass) : 0;
            metrics.put(aClass, metric + 1);
        }
    }
}