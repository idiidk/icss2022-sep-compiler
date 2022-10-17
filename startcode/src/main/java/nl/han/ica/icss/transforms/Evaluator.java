package nl.han.ica.icss.transforms;

import nl.han.ica.datastructures.SymbolTable;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.BoolLiteral;
import nl.han.ica.icss.ast.literals.PercentageLiteral;
import nl.han.ica.icss.ast.literals.PixelLiteral;
import nl.han.ica.icss.ast.literals.ScalarLiteral;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;

import java.util.ArrayList;

public class Evaluator implements Transform {
    private SymbolTable<String, Literal> variableValues;

    @Override
    public void apply(AST ast) {
        variableValues = new SymbolTable<>();
        transformNode(ast.root);
    }

    private void transformNode(ASTNode node) {
        if (node instanceof Stylesheet) {
            transformStylesheet((Stylesheet) node);
        } else if (node instanceof Stylerule) {
            transformStylerule((Stylerule) node);
        } else if (node instanceof VariableAssignment) {
            transformVariableAssignment((VariableAssignment) node);
        } else if (node instanceof Declaration) {
            transformDeclaration((Declaration) node);
        } else if (node instanceof IfClause) {
            transformIfClause((IfClause) node);
        } else {
            System.out.println("Transform not implemented for node of type: " + node.getNodeLabel());
        }
    }

    private void transformStylesheet(Stylesheet node) {
        variableValues.pushScope();

        for (ASTNode child : node.body) {
            transformNode(child);
        }

        variableValues.popScope();
    }

    private void transformStylerule(Stylerule node) {
        variableValues.pushScope();

        for (ASTNode child : node.body) {
            transformNode(child);
        }

        variableValues.popScope();
    }

    private void transformIfClause(IfClause node) {
        node.conditionalExpression = getLiteralFromExpression(node.conditionalExpression);
        boolean result = ((BoolLiteral) node.conditionalExpression).value;

        if (result) {
            // Remove the else clause if the expression evaluates to true
            if (node.elseClause != null) {
                node.elseClause.body = new ArrayList<>();
            }
        } else {
            if (node.elseClause != null) {
                // Swap the if and else clause bodies if an else clause exists
                node.body = node.elseClause.body;
                node.elseClause.body = new ArrayList<>();
            } else {
                System.out.println("bad monkey");
                // Remove the if clause body if the expression evaluates to false
                // and there is no else clause
                node.body = new ArrayList<>();
            }
        }

        variableValues.pushScope();
        for(ASTNode child : node.body) {
            transformNode(child);
        }
        variableValues.popScope();
    }

    private void transformVariableAssignment(VariableAssignment node) {
        node.expression = getLiteralFromExpression(node.expression);
        variableValues.putVariable(node.name.name, (Literal) node.expression);
    }

    private void transformDeclaration(Declaration node) {
        node.expression = getLiteralFromExpression(node.expression);
    }

    private Literal getLiteralFromExpression(Expression node) {
        if (node instanceof Operation) {
            return getLiteralFromOperation((Operation) node);
        }

        if (node instanceof VariableReference) {
            return variableValues.getVariable(((VariableReference) node).name);
        }

        return (Literal) node;
    }

    private Literal getLiteralFromOperation(Operation node) {
        Literal left;
        Literal right;

        if (node.lhs instanceof Operation) {
            return getLiteralFromOperation((Operation) node.lhs);
        } else if (node.lhs instanceof VariableReference) {
            left = variableValues.getVariable(((VariableReference) node.lhs).name);
        } else {
            left = (Literal) node.lhs;
        }

        if (node.rhs instanceof Operation) {
            return getLiteralFromOperation((Operation) node.rhs);
        } else if (node.rhs instanceof VariableReference) {
            right = variableValues.getVariable(((VariableReference) node.rhs).name);
        } else {
            right = (Literal) node.rhs;
        }

        int leftValue = getLiteralValue(left);
        int rightValue = getLiteralValue(right);

        if (node instanceof AddOperation) {
            return cloneLiteralWithValue(left, leftValue + rightValue);
        } else if (node instanceof SubtractOperation) {
            return cloneLiteralWithValue(left, leftValue - rightValue);
        } else if (node instanceof MultiplyOperation) {
            return cloneLiteralWithValue(left, leftValue * rightValue);
        } else {
            node.setError("Operation not implemented?");
            return null;
        }
    }

    private int getLiteralValue(Literal literal) {
        if (literal instanceof PixelLiteral) {
            return ((PixelLiteral) literal).value;
        } else if (literal instanceof ScalarLiteral) {
            return ((ScalarLiteral) literal).value;
        } else {
            return ((PercentageLiteral) literal).value;
        }
    }

    private Literal cloneLiteralWithValue(Literal literal, int value) {
        if (literal instanceof PixelLiteral) {
            return new PixelLiteral(value);
        } else if (literal instanceof ScalarLiteral) {
            return new ScalarLiteral(value);
        } else {
            return new PercentageLiteral(value);
        }
    }

}
