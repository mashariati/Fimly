package com.fimly.app.presentation.core

/**
 * Base marker interface for UI State.
 * Represents the complete state of a screen at any given moment.
 */
interface UiState

/**
 * Base marker interface for UI Intents.
 * Represents user actions (e.g., clicks) or system events (e.g., lifecycle).
 */
interface UiIntent

/**
 * Base marker interface for UI Effects (Side Effects).
 * Represents one-time events that should not be persisted in state
 * (e.g., Navigation, showing a Toast or Snackbar).
 */
interface UiEffect