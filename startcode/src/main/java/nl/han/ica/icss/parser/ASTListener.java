package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.HANStack;
import nl.han.ica.datastructures.IHANStack;
import nl.han.ica.icss.ast.*;
import nl.han.ica.icss.ast.literals.*;
import nl.han.ica.icss.ast.operations.AddOperation;
import nl.han.ica.icss.ast.operations.MultiplyOperation;
import nl.han.ica.icss.ast.operations.SubtractOperation;
import nl.han.ica.icss.ast.selectors.ClassSelector;
import nl.han.ica.icss.ast.selectors.IdSelector;
import nl.han.ica.icss.ast.selectors.TagSelector;

/**
 * This class extracts the ICSS Abstract Syntax Tree from the Antlr Parse tree.
 */
public class ASTListener extends ICSSBaseListener {

    //Accumulator attributes:
    private AST ast;

    //Use this to keep track of the parent nodes when recursively traversing the ast
    private IHANStack<ASTNode> currentContainer;

    public ASTListener() {
        ast = new AST();
        currentContainer = new HANStack<>();
    }

    public AST getAST() {
        return ast;
    }

    // Expressions :D


    @Override
    public void enterExpression(ICSSParser.ExpressionContext ctx) {
        // If the expression has three children, for example imagine the children like this: (5, *, 5) = length 3
        if (ctx.getChildCount() == 3) {
            // Get the first child which should be the operator and add the appropriate operation to our stack
            switch(ctx.getChild(1).getText()) {
                case "*":
                    currentContainer.push(new MultiplyOperation());
                    break;
                case "+":
                    currentContainer.push(new AddOperation());
                    break;
                case "-":
                    currentContainer.push(new SubtractOperation());
                    break;
            }
        }

        super.enterExpression(ctx);
    }

    @Override
    public void exitExpression(ICSSParser.ExpressionContext ctx) {
        // Only pop the expression if it has a terminal node
        if (ctx.MUL() != null || ctx.PLUS() != null || ctx.MIN() != null) {
            ASTNode current = currentContainer.pop();
            currentContainer.peek().addChild(current);
        }

        super.exitExpression(ctx);
    }

    // Stylesheet
    @Override
    public void enterStylesheet(ICSSParser.StylesheetContext ctx) {
        currentContainer.push(new Stylesheet());
        super.enterStylesheet(ctx);
    }

    @Override
    public void exitStylesheet(ICSSParser.StylesheetContext ctx) {
        ast.setRoot((Stylesheet) currentContainer.pop());
        super.exitStylesheet(ctx);
    }

    // StyleRule
    @Override
    public void enterStyleRule(ICSSParser.StyleRuleContext ctx) {
        currentContainer.push(new Stylerule());
        super.enterStyleRule(ctx);
    }

    @Override
    public void exitStyleRule(ICSSParser.StyleRuleContext ctx) {
        ASTNode current = currentContainer.pop();
        currentContainer.peek().addChild(current);
        super.exitStyleRule(ctx);
    }

    // Selectors
    // TagSelector

    @Override
    public void enterTagSelector(ICSSParser.TagSelectorContext ctx) {
        currentContainer.push(new TagSelector(ctx.getText()));
        super.enterTagSelector(ctx);
    }

    @Override
    public void exitTagSelector(ICSSParser.TagSelectorContext ctx) {
        ASTNode current = currentContainer.pop();
        currentContainer.peek().addChild(current);
        super.exitTagSelector(ctx);
    }

    // IdSelector

    @Override
    public void enterIdSelector(ICSSParser.IdSelectorContext ctx) {
        currentContainer.push(new IdSelector(ctx.getText()));
        super.enterIdSelector(ctx);
    }

    @Override
    public void exitIdSelector(ICSSParser.IdSelectorContext ctx) {
        ASTNode current = currentContainer.pop();
        currentContainer.peek().addChild(current);
        super.exitIdSelector(ctx);
    }

    // ClassSelector

    @Override
    public void enterClassSelector(ICSSParser.ClassSelectorContext ctx) {
        currentContainer.push(new ClassSelector(ctx.getText()));
        super.enterClassSelector(ctx);
    }

    @Override
    public void exitClassSelector(ICSSParser.ClassSelectorContext ctx) {
        ASTNode current = currentContainer.pop();
        currentContainer.peek().addChild(current);
        super.exitClassSelector(ctx);
    }

    // VariableReference
    // this one's special because it doesn't get added to the AST,
    // it's just a reference to the current var that's added to the top stack child

    @Override
    public void enterVariableReference(ICSSParser.VariableReferenceContext ctx) {
        currentContainer.peek().addChild(new VariableReference(ctx.getText()));
        super.enterVariableReference(ctx);
    }

    // VariableAssignment

    @Override
    public void enterVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        currentContainer.push(new VariableAssignment());
        super.enterVariableAssignment(ctx);
    }

    @Override
    public void exitVariableAssignment(ICSSParser.VariableAssignmentContext ctx) {
        ASTNode current = currentContainer.pop();
        currentContainer.peek().addChild(current);
        super.exitVariableAssignment(ctx);
    }

    // Declaration

    @Override
    public void enterDeclaration(ICSSParser.DeclarationContext ctx) {
        currentContainer.push(new Declaration());
        super.enterDeclaration(ctx);
    }

    @Override
    public void exitDeclaration(ICSSParser.DeclarationContext ctx) {
        ASTNode current = currentContainer.pop();
        currentContainer.peek().addChild(current);
        super.exitDeclaration(ctx);
    }

    // PropertyName

    @Override
    public void enterPropertyName(ICSSParser.PropertyNameContext ctx) {
        currentContainer.push(new PropertyName(ctx.getText()));
        super.enterPropertyName(ctx);
    }

    @Override
    public void exitPropertyName(ICSSParser.PropertyNameContext ctx) {
        ASTNode current = currentContainer.pop();
        currentContainer.peek().addChild(current);
        super.exitPropertyName(ctx);
    }

    // Literals
    // ColorLiteral

    @Override
    public void enterColorLiteral(ICSSParser.ColorLiteralContext ctx) {
        currentContainer.push(new ColorLiteral(ctx.getText()));
        super.enterColorLiteral(ctx);
    }

    @Override
    public void exitColorLiteral(ICSSParser.ColorLiteralContext ctx) {
        ASTNode current = currentContainer.pop();
        currentContainer.peek().addChild(current);
        super.exitColorLiteral(ctx);
    }

    // BoolLiteral

    @Override
    public void enterBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
        currentContainer.push(new BoolLiteral(ctx.getText()));
        super.enterBoolLiteral(ctx);
    }

    @Override
    public void exitBoolLiteral(ICSSParser.BoolLiteralContext ctx) {
        ASTNode current = currentContainer.pop();
        currentContainer.peek().addChild(current);
        super.exitBoolLiteral(ctx);
    }

    // PercentageLiteral

    @Override
    public void enterPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
        currentContainer.push(new PercentageLiteral(ctx.getText()));
        super.enterPercentageLiteral(ctx);
    }

    @Override
    public void exitPercentageLiteral(ICSSParser.PercentageLiteralContext ctx) {
        ASTNode current = currentContainer.pop();
        currentContainer.peek().addChild(current);
        super.exitPercentageLiteral(ctx);
    }

    // PixelLiteral

    @Override
    public void enterPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
        currentContainer.push(new PixelLiteral(ctx.getText()));
        super.enterPixelLiteral(ctx);
    }

    @Override
    public void exitPixelLiteral(ICSSParser.PixelLiteralContext ctx) {
        ASTNode current = currentContainer.pop();
        currentContainer.peek().addChild(current);
        super.exitPixelLiteral(ctx);
    }


    // ScalarLiteral

    @Override
    public void enterScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
        currentContainer.push(new ScalarLiteral(ctx.getText()));
        super.enterScalarLiteral(ctx);
    }

    @Override
    public void exitScalarLiteral(ICSSParser.ScalarLiteralContext ctx) {
        ASTNode current = currentContainer.pop();
        currentContainer.peek().addChild(current);
        super.exitScalarLiteral(ctx);
    }

    // Clauses
    // IfClause

    @Override
    public void enterIfClause(ICSSParser.IfClauseContext ctx) {
        currentContainer.push(new IfClause());
        super.enterIfClause(ctx);
    }

    @Override
    public void exitIfClause(ICSSParser.IfClauseContext ctx) {
        ASTNode current = currentContainer.pop();
        currentContainer.peek().addChild(current);
        super.exitIfClause(ctx);
    }

    // ElseClause
    @Override
    public void enterElseClause(ICSSParser.ElseClauseContext ctx) {
        currentContainer.push(new ElseClause());
        super.enterElseClause(ctx);
    }

    @Override
    public void exitElseClause(ICSSParser.ElseClauseContext ctx) {
        ASTNode current = currentContainer.pop();
        currentContainer.peek().addChild(current);
        super.exitElseClause(ctx);
    }
}