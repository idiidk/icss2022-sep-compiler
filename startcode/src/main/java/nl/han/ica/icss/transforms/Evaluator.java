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
    private ArrayList<ASTNode> toAdd;
    ArrayList<ASTNode> newTree;

    @Override
    public void apply(AST ast) {
        toAdd = new ArrayList<>();
        newTree = new ArrayList<>();
        variableValues = new SymbolTable<>();

        variableValues.pushScope();
        recursiveChildTransform(ast.root);
        variableValues.popScope();

        ast.root.body = newTree;
    }

    public void recursiveChildTransform(ASTNode parentNode) {
        for (ASTNode child : parentNode.getChildren()) {
            toAdd.add(child);

            if (child instanceof Stylerule) {
                toAdd = new ArrayList<>();

                variableValues.pushScope();
                recursiveChildTransform(child);
                variableValues.popScope();

                Stylerule newStylerule = new Stylerule();
                newStylerule.body = toAdd;
                newTree.add(newStylerule);
            } else if (child instanceof VariableAssignment) {
                transformVariableAssignment((VariableAssignment) child);
            } else if (child instanceof Declaration) {
                transformDeclaration((Declaration) child);
            } else if (child instanceof IfClause) {
                transformIfClause((IfClause) child);
                toAdd.remove(child);
            }
        }
    }

    public void transformVariableAssignment(VariableAssignment assignment) {
        assignment.expression = evaluateExpression(assignment.expression);
        variableValues.putVariable(assignment.name.name, (Literal) assignment.expression);
    }

    public void transformDeclaration(Declaration declaration) {
        declaration.expression = evaluateExpression(declaration.expression);
    }

    public void transformIfClause(IfClause clause) {
        BoolLiteral result = (BoolLiteral) evaluateExpression(clause.conditionalExpression);
        System.out.println(result);

        if (result.value) {
            // Swap the body of the if statement with the parent node after it was parsed
            clause.elseClause = null;
        } else {
            if (clause.elseClause != null) {
                clause.body = clause.elseClause.body;
                clause.elseClause = null;
            } else {
                clause.body = new ArrayList<>();
            }
        }

        recursiveChildTransform(clause);
    }

    public Literal evaluateExpression(Expression expression) {
        if (expression instanceof Operation) {
            return evaluateOperation((Operation) expression);
        }

        if (expression instanceof VariableReference) {
            return variableValues.getVariable(((VariableReference) expression).name);
        }

        return (Literal) expression;
    }

    public Literal evaluateOperation(Operation operation) {
        Literal left = evaluateExpression(operation.lhs);
        Literal right = evaluateExpression(operation.rhs);

        int leftValue = (int) left.getValue();
        int rightValue = (int) right.getValue();

        if (operation instanceof MultiplyOperation) {
            return cloneLiteralWithValue(left, leftValue * rightValue);
        }

        if (operation instanceof AddOperation) {
            return cloneLiteralWithValue(left, leftValue + rightValue);
        }

        if (operation instanceof SubtractOperation) {
            return cloneLiteralWithValue(left, leftValue - rightValue);
        }

        throw new RuntimeException("Evaluation type not found");
    }

    private Literal cloneLiteralWithValue(Literal literal, int value) {
        if (literal instanceof PixelLiteral) {
            return new PixelLiteral(value);
        }

        if (literal instanceof ScalarLiteral) {
            return new ScalarLiteral(value);
        }

        if (literal instanceof PercentageLiteral) {
            return new PercentageLiteral(value);
        }

        throw new RuntimeException("Literal to clone not found");
    }
}
