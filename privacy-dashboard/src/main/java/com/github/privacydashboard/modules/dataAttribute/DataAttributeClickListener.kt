package com.github.privacydashboard.modules.dataAttribute

import com.github.privacydashboard.models.base.attribute.DataAttribute


interface DataAttributeClickListener {
    fun onAttributeClick(dataAttribute: DataAttribute?)
}