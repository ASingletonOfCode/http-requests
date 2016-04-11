package com.budjb.httprequests

class FormData {
    /**
     * Map containing the values of the form.
     */
    private Map<String, List<String>> data = [:]

    /**
     * Adds a form field.
     *
     * @param name
     * @param value
     * @return
     */
    FormData addField(String name, String value) {
        if (!data.containsKey(name) || !(data.get(name) instanceof List)) {
            data.put(name, [])
        }
        data.get(name).add(value)
        return this
    }

    /**
     * Adds a form field with multiple values.
     *
     * @param name
     * @param values
     * @return
     */
    FormData addField(String name, List<String> values) {
        values.each {
            addField(name, it)
        }
        return this
    }

    /**
     * Adds many elements to the form.
     *
     * @param data
     * @return
     */
    FormData addFields(Map<String, Object> data) {
        data.each { name, values ->
            if (values instanceof List) {
                addField(name, values)
            }
            else {
                addField(name, values.toString())
            }
        }
        return this
    }

    /**
     * Return the form elements as a map, where the key is the name of the form field and the value is a list of values
     * for the form element, even if there is only one value.
     *
     * @return
     */
    Map<String, List<String>> getElements() {
        return data
    }

    /**
     * Return the form elements as map, where the key is the name of the form field and the value is either a String or
     * a List of Strings if the field has multiple values.
     *
     * @return
     */
    Map<String, Object> getFlattenedElements() {
        return data.collectEntries { name, values ->
            if (values.size() == 1) {
                return [name: values.get(0)]
            }
            else {
                return [name: values]
            }
        } as Map<String, Object>
    }
}