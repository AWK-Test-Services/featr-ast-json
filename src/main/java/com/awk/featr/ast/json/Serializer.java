package com.awk.featr.ast.json;

import com.awk.featr.ast.*;

import java.util.List;

public class Serializer {
    public static String ToJson(Document document)
    {
        JsonBuilder builder =  new JsonBuilder()
                .withOpenTag()
                .withString("id", document.getId())
                .withString("language", document.getLanguage())
                .withObject("feature", FeatureToJson(document.getFeature()));

        if ( !document.getComment().isEmpty() ) builder.withString("comment", document.getComment());

        return builder.withClosingTag().build();
    }

    private static String FeatureToJson(Feature feature) {
        JsonBuilder builder = new JsonBuilder()
                .withString("id", feature.getId())
                .withString("name", feature.getName())
                .withString("description", convertNewlines(feature.getDescription()))
            ;

        if ( feature.getRule() != null ) builder.withString("rule", feature.getRule());
        if ( !feature.getTags().isEmpty() ) builder.withArray("tags", TagsToString(feature.getTags()));
        if ( feature.getBackground() != null ) builder.withObject("background", ScenarioDefinitionToJson(feature.getBackground()));
        if ( !feature.getScenarioDefinitions().isEmpty() ) builder.withArray("scenarioDefinitions", ScenarioDefinitionsToString(feature.getScenarioDefinitions()));

        return builder.build();
    }

    private static String TagsToString(List<String> tags) {
        StringBuilder tagsString = new StringBuilder();
        tagsString.append(tags.remove(0));
        tags.forEach(tag -> tagsString.append(", ").append(tag));

        return tagsString.toString();
    }

    private static String ScenarioDefinitionsToString(List<ScenarioDefinition> scenarioDefinitions) {
        StringBuilder scenarioDefinitionsString = new StringBuilder();

        scenarioDefinitionsString.append("{").append(ScenarioDefinitionToJson(scenarioDefinitions.remove(0))).append("}");
        scenarioDefinitions.forEach(scenarioDefinition -> scenarioDefinitionsString.append(", {").append(ScenarioDefinitionToJson(scenarioDefinition)).append("}"));

        return scenarioDefinitionsString.toString();
    }

    private static String ScenarioDefinitionToJson(ScenarioDefinition scenarioDefinition) {
        JsonBuilder builder = new JsonBuilder().withString("type", scenarioDefinition.getType());

        if ( !scenarioDefinition.getName().isEmpty() ) builder.withString("name", scenarioDefinition.getName());
        if ( !scenarioDefinition.getDescription().isEmpty() ) builder.withString("description", convertNewlines(scenarioDefinition.getDescription()));
        if ( !scenarioDefinition.getSteps().isEmpty() ) builder.withArray("steps", StepsToArray(scenarioDefinition.getSteps()));

        if (scenarioDefinition.getClass().equals(Scenario.class))
            return ScenarioToJson( builder, (Scenario) scenarioDefinition );
        if (scenarioDefinition.getClass().equals(ScenarioOutline.class))
            return ScenarioOutlineToJson( builder, (ScenarioOutline) scenarioDefinition );
        return builder.build(); // Background
    }

    private static String ScenarioToJson(JsonBuilder builder, Scenario scenario) {
        if ( !scenario.getTags().isEmpty() ) builder.withArray("tags", TagsToString(scenario.getTags()));

        return builder.build();
    }

    private static String ScenarioOutlineToJson(JsonBuilder builder, ScenarioOutline scenarioOutline) {
        if ( !scenarioOutline.getTags().isEmpty() ) builder.withArray("tags", TagsToString(scenarioOutline.getTags()));
        if ( scenarioOutline.getExamples() != null ) builder.withObject("examples", ExamplesToJson(scenarioOutline.getExamples()));

        return builder.build();
    }

    private static String ExamplesToJson(Examples examples) {
        JsonBuilder builder = new JsonBuilder()
                .withString("keyword", examples.getKeyword())
                .withArray("header", cellsToString(examples.getHeader().getValues()));

        if ( !examples.getName().isEmpty() ) builder.withString("name", examples.getName());
        if ( !examples.getDescription().isEmpty() ) builder.withString("description", convertNewlines(examples.getDescription()));
        if ( !examples.getTags().isEmpty() ) builder.withArray("tags", TagsToString(examples.getTags()));
        if ( !examples.getExamples().isEmpty() ) builder.withArray("rows", RowsToJson(examples.getExamples()));

        return builder.build();
    }

    private static String StepsToArray(List<Step> steps) {
        StringBuilder stepsString = new StringBuilder();
        stepsString.append(StepToString(steps.remove(0)));
        steps.forEach(step -> stepsString.append(", ").append(StepToString(step)));

        return stepsString.toString();
    }

    private static String StepToString(Step step) {
        JsonBuilder builder = new JsonBuilder()
                .withOpenTag()
                .withString("keyword", step.getKeyword())
                .withString("text", step.getText());

        if ( step.getArgument() != null) builder.withObject("argument", StepArgumentToJson(step.getArgument()));

        return builder.withClosingTag().build();
    }

    private static String StepArgumentToJson(StepArgument argument) {

        if (argument.getClass().equals(DocString.class))
            return DocStringToJson( (DocString) argument );
        return DataTableToJson( (DataTable) argument );
    }

    private static String DataTableToJson(DataTable dataTable) {
        JsonBuilder builder = new JsonBuilder()
                .withString("type", DataTable.class.getSimpleName())
                .withArray("rows", RowsToJson(dataTable.getRows()));

        return builder.build();
    }

    private static String RowsToJson(List<TableRow> rows) {

        StringBuilder rowsString = new StringBuilder();
        rowsString.append(RowToJson(rows.remove(0)));
        rows.forEach(row -> rowsString.append(", ").append(RowToJson(row)));

        return rowsString.toString();
    }

    private static String RowToJson(TableRow row) {
        return ToArray(cellsToString(row.getValues()));
    }

    private static String ToArray(String value) {
        StringBuilder builder = new StringBuilder()
                .append("[")
                .append(value)
                .append("]");

        return builder.toString();
    }

    private static String cellsToString(List<String> cells) {
        StringBuilder cellsString = new StringBuilder();
        cellsString.append(stringWithQuotes(cells.remove(0)));
        cells.forEach(cell -> cellsString.append(", ").append(stringWithQuotes(cell)));

        return cellsString.toString();
    }

    private static String DocStringToJson(DocString docString) {
        JsonBuilder builder = new JsonBuilder()
                .withString("type", DocString.class.getSimpleName())
                .withString("contentType", docString.getType())
                .withString("content", convertNewlines(docString.getContent()));

        return builder.build();
    }

    private static String stringWithQuotes(String value) {
        StringBuilder builder = new StringBuilder()
                .append("\"")
                .append(value)
                .append("\"");
        return builder.toString();
    }


    private static String convertNewlines(String value) {
        return value.replace("\n", "\\n");
    }
}
