package com.akjostudios.acsp.bot.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class JsonPrettyPrinter extends DefaultPrettyPrinter {
    public JsonPrettyPrinter() {
        _arrayIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
        _objectIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
    }

    public JsonPrettyPrinter(DefaultPrettyPrinter base) {
        super(base);
    }

    @Override
    public JsonPrettyPrinter createInstance() {
        if (getClass() != JsonPrettyPrinter.class) {
            throw new IllegalStateException("Failed `createInstance()`: " + getClass().getName()
                    + " does not override method; it has to");
        }
        return new JsonPrettyPrinter(this);
    }

    @Override
    public void writeObjectFieldValueSeparator(@NotNull JsonGenerator jg) throws IOException {
        jg.writeRaw(": ");
    }

    @Override
    public JsonPrettyPrinter withSeparators(@NotNull Separators separators) {
        this._separators = separators;
        this._objectFieldValueSeparatorWithSpaces = separators.getObjectFieldValueSeparator() + " ";
        return this;
    }

    @Override
    public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException {
        if (!_arrayIndenter.isInline()) {
            --_nesting;
        }
        if (nrOfValues > 0) {
            _arrayIndenter.writeIndentation(g, _nesting);
        }
        g.writeRaw(']');
    }

    @Override
    public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
        if (!_objectIndenter.isInline()) {
            --_nesting;
        }
        if (nrOfEntries > 0) {
            _objectIndenter.writeIndentation(g, _nesting);
        }
        g.writeRaw('}');
    }
}