package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VaultCategory
import com.example.data.model.VaultRecord
import com.example.ui.components.VaultRecordItem
import com.example.ui.theme.VaultBackground
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultBorderHighlight
import com.example.ui.theme.VaultEmerald
import com.example.ui.theme.VaultEmeraldBright
import com.example.ui.theme.VaultEmeraldGlow
import com.example.ui.theme.VaultGold
import com.example.ui.theme.VaultSurface
import com.example.ui.theme.VaultSurfaceElevated
import com.example.ui.theme.VaultSurfaceHighlight
import com.example.ui.theme.VaultTextMuted
import com.example.ui.theme.VaultTextPrimary
import com.example.ui.theme.VaultTextSecondary

@Composable
fun VaultScreen(
    records: List<VaultRecord>,
    selectedCategory: String?,
    searchQuery: String,
    onlyFavorites: Boolean,
    onCategorySelect: (String?) -> Unit,
    onSearchChange: (String) -> Unit,
    onToggleFavorites: () -> Unit,
    onAddRecordClick: () -> Unit,
    onEditRecordClick: (VaultRecord) -> Unit,
    onDeleteRecordClick: (VaultRecord) -> Unit,
    onToggleRecordFavorite: (VaultRecord) -> Unit,
    onCopyValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VaultBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 900.dp)
                .align(Alignment.TopCenter)
        ) {
            // Screen Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (selectedCategory != null) {
                                VaultCategory.find(selectedCategory).name
                            } else {
                                "Vault Compartments"
                            },
                            color = VaultTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (selectedCategory != null) {
                                VaultCategory.find(selectedCategory).description
                            } else {
                                "${records.size} active encrypted secrets"
                            },
                            color = VaultTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    // VIP Filter button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (onlyFavorites) VaultGold.copy(alpha = 0.15f) else VaultSurfaceElevated
                            )
                            .border(
                                1.dp,
                                if (onlyFavorites) VaultGold else VaultBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable(onClick = onToggleFavorites)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("filter_favorites_toggle"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (onlyFavorites) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "VIP Filter",
                                tint = if (onlyFavorites) VaultGold else VaultTextSecondary,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "VIP",
                                color = if (onlyFavorites) VaultGold else VaultTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("vault_search_input"),
                    placeholder = {
                        Text(
                            text = "Search records, accounts, tokens...",
                            color = VaultTextMuted,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = VaultTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Clear",
                                    tint = VaultTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VaultEmerald,
                        unfocusedBorderColor = VaultBorder,
                        focusedContainerColor = VaultSurfaceElevated,
                        unfocusedContainerColor = VaultSurface,
                        cursorColor = VaultEmeraldBright,
                        focusedTextColor = VaultTextPrimary,
                        unfocusedTextColor = VaultTextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Compartment Horizontal Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CategoryChip(
                        name = "All Records",
                        isSelected = selectedCategory == null,
                        onClick = { onCategorySelect(null) },
                        testTag = "filter_chip_all"
                    )

                    for (category in VaultCategory.ALL) {
                        CategoryChip(
                            name = category.name,
                            isSelected = selectedCategory.equals(category.id, ignoreCase = true),
                            onClick = {
                                if (selectedCategory.equals(category.id, ignoreCase = true)) {
                                    onCategorySelect(null)
                                } else {
                                    onCategorySelect(category.id)
                                }
                            },
                            testTag = "filter_chip_${category.id.lowercase()}"
                        )
                    }
                }
            }

            // Records List or Empty State
            if (records.isEmpty()) {
                EmptyVaultState(
                    selectedCategory = selectedCategory,
                    hasSearch = searchQuery.isNotBlank(),
                    onAddRecordClick = onAddRecordClick,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(records, key = { it.id }) { record ->
                        VaultRecordItem(
                            record = record,
                            onEditClick = { onEditRecordClick(record) },
                            onDeleteClick = { onDeleteRecordClick(record) },
                            onToggleFavorite = { onToggleRecordFavorite(record) },
                            onCopyValue = onCopyValue
                        )
                    }
                }
            }
        }

        // Floating Action Button to Add Record
        FloatingActionButton(
            onClick = onAddRecordClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 84.dp)
                .testTag("fab_add_record"),
            containerColor = VaultEmerald,
            contentColor = VaultBackground,
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Record",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun CategoryChip(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) VaultEmerald.copy(alpha = 0.15f) else VaultSurfaceElevated
            )
            .border(
                1.dp,
                if (isSelected) VaultEmerald else VaultBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag(testTag)
    ) {
        Text(
            text = name,
            color = if (isSelected) VaultEmeraldBright else VaultTextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun EmptyVaultState(
    selectedCategory: String?,
    hasSearch: Boolean,
    onAddRecordClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(VaultSurfaceElevated)
                .border(1.dp, VaultBorder, RoundedCornerShape(20.dp))
                .padding(32.dp)
        ) {
            // Vault Shield Lock Visual
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(VaultEmeraldGlow, VaultSurface)
                        )
                    )
                    .border(1.dp, VaultBorderHighlight, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = null,
                    tint = VaultEmeraldBright,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = if (hasSearch) "No Matching Records" else "Your Vault Compartment is Empty",
                color = VaultTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (hasSearch) {
                    "No items match your search query. Try broadening your keywords."
                } else {
                    "Store and safeguard sensitive credentials, private bank numbers, passwords, and crypto recovery phrases."
                },
                color = VaultTextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.widthIn(max = 280.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Add Record Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(VaultEmerald)
                    .clickable(onClick = onAddRecordClick)
                    .padding(horizontal = 20.dp, vertical = 11.dp)
                    .testTag("empty_state_add_button"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = VaultBackground,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Add Secret Record",
                    color = VaultBackground,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
